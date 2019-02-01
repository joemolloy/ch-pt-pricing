package ch.ethz.matsim.ch_pt_utils.server;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.inject.Provider;

import org.geotools.referencing.CRS;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.NoninvertibleTransformException;

import ch.ethz.matsim.baseline_scenario.transit.connection.DefaultTransitConnectionFinder;
import ch.ethz.matsim.baseline_scenario.transit.connection.TransitConnectionFinder;
import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRouter;
import ch.ethz.matsim.baseline_scenario.transit.routing.EnrichedTransitRouter;
import ch.ethz.matsim.baseline_scenario.zurich.cutter.utils.DefaultDepartureFinder;
import ch.ethz.matsim.baseline_scenario.zurich.cutter.utils.DepartureFinder;
import ch.ethz.matsim.ch_pt_utils.cost.TransitCostCalculator;
import ch.ethz.matsim.ch_pt_utils.cost.sbb.RailTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.sbb.SBBTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.sbb.data.InterchangeReader;
import ch.ethz.matsim.ch_pt_utils.cost.sbb.data.Triangle;
import ch.ethz.matsim.ch_pt_utils.cost.sbb.data.TriangleReader;
import ch.ethz.matsim.ch_pt_utils.cost.sbb.data.TriangleRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStageTransformer;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.CompositeZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalReader;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators.AWelleTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators.EngadinMobilTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators.FrimobilTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators.LiberoTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators.OstwindTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators.PassepartoutTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators.TVZGTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators.TransRenoTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators.UniresoTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators.ZVVTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.server.routing.RoutingHandler;
import ch.sbb.matsim.routing.pt.raptor.DefaultRaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.DefaultRaptorParametersForPerson;
import ch.sbb.matsim.routing.pt.raptor.LeastCostRaptorRouteSelector;
import ch.sbb.matsim.routing.pt.raptor.RaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.RaptorParametersForPerson;
import ch.sbb.matsim.routing.pt.raptor.RaptorRouteSelector;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptor;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorFactory;
import io.javalin.Javalin;

public class RunRoutingServer {
	static public void main(String[] args)
			throws NoSuchAuthorityCodeException, FactoryException, NoninvertibleTransformException, IOException {
		int port = Integer.parseInt(args[0]);
		File transitSchedulePath = new File(args[1]);
		File networkPath = new File(args[2]);
		File zonesPath = new File(args[3]);
		File trianglesPath = new File(args[4]);
		File interchangesPath = new File(args[5]);

		// Read schedule data
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);

		new TransitScheduleReader(scenario).readFile(transitSchedulePath.toString());
		new MatsimNetworkReader(scenario.getNetwork()).readFile(networkPath.toString());

		double additionalTransferTime = 30.0;
		config.transitRouter().setAdditionalTransferTime(additionalTransferTime);

		// Set up RAPTOR
		double walkDistanceFactor = config.plansCalcRoute().getOrCreateModeRoutingParams("walk")
				.getBeelineDistanceFactor();

		RaptorParametersForPerson raptorParametersForPerson = new DefaultRaptorParametersForPerson(config);
		RaptorIntermodalAccessEgress raptorIntermodalAccessEgress = new DefaultRaptorIntermodalAccessEgress();
		RaptorRouteSelector raptorRouteSelector = new LeastCostRaptorRouteSelector();
		Map<String, Provider<RoutingModule>> routingModuleProviders = Collections.emptyMap();

		SwissRailRaptorFactory factory = new SwissRailRaptorFactory(scenario.getTransitSchedule(), config,
				scenario.getNetwork(), raptorParametersForPerson, raptorRouteSelector, raptorIntermodalAccessEgress,
				config.plans(), scenario.getPopulation(), routingModuleProviders);

		SwissRailRaptor swissRailRaptor = factory.get();

		DepartureFinder departureFinder = new DefaultDepartureFinder();
		TransitConnectionFinder connectionFinder = new DefaultTransitConnectionFinder(departureFinder);

		EnrichedTransitRouter enrichedTransitRouter = new DefaultEnrichedTransitRouter(swissRailRaptor,
				scenario.getTransitSchedule(), connectionFinder, scenario.getNetwork(), walkDistanceFactor, 0.0);

		// Set up cost calculation

		ZonalReader zonalReader = new ZonalReader();
		Collection<Authority> authorities = zonalReader.readAuthorities(zonesPath);
		Collection<Zone> zones = zonalReader.readZones(zonesPath, authorities);

		ZonalRegistry zonalRegistry = new ZonalRegistry(authorities, zones);

		CompositeZonalTicketGenerator zonalTicketGenerator = new CompositeZonalTicketGenerator();
		zonalTicketGenerator.addGenerator(AWelleTicketGenerator.create(zonalRegistry));
		zonalTicketGenerator.addGenerator(EngadinMobilTicketGenerator.create(zonalRegistry));
		zonalTicketGenerator.addGenerator(FrimobilTicketGenerator.create(zonalRegistry));
		zonalTicketGenerator.addGenerator(LiberoTicketGenerator.create(zonalRegistry));
		zonalTicketGenerator.addGenerator(OstwindTicketGenerator.create(zonalRegistry));
		zonalTicketGenerator.addGenerator(PassepartoutTicketGenerator.create(zonalRegistry));
		zonalTicketGenerator.addGenerator(TransRenoTicketGenerator.create(zonalRegistry));
		zonalTicketGenerator.addGenerator(TVZGTicketGenerator.create(zonalRegistry));
		zonalTicketGenerator.addGenerator(UniresoTicketGenerator.create(zonalRegistry));
		zonalTicketGenerator.addGenerator(ZVVTicketGenerator.create(zonalRegistry));

		TriangleReader triangleReader = new TriangleReader();
		Collection<Triangle> triangles = triangleReader.read(trianglesPath);

		InterchangeReader interchangeReader = new InterchangeReader();
		Collection<Long> interchangeIds = interchangeReader.read(interchangesPath);

		TriangleRegistry triangleRegistry = new TriangleRegistry(triangles, interchangeIds);

		RailTicketGenerator railTicketGenerator = new SBBTicketGenerator(triangleRegistry, zonalRegistry);

		TransitCostCalculator transitCostCalculator = new TransitCostCalculator(zonalRegistry, zonalTicketGenerator,
				railTicketGenerator);

		Collection<String> railModes = Collections.singleton("rail");
		TransitStageTransformer transformer = new TransitStageTransformer(scenario.getTransitSchedule(), railModes);

		Javalin app = Javalin.create();
		app.enableCorsForAllOrigins();
		app.post("/api", new RoutingHandler(enrichedTransitRouter, scenario.getNetwork(), scenario.getTransitSchedule(),
				transitCostCalculator, transformer, CRS.decode("EPSG:2056")));
		app.get("/", new FrontendHandler());
		app.start(port);
	}
}
