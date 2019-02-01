package ch.ethz.matsim.ch_pt_utils.run;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Provider;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.router.LinkWrapperFacility;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.misc.Time;
import org.matsim.facilities.Facility;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

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
import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.solver.TicketSolver;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;
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
import ch.sbb.matsim.routing.pt.raptor.DefaultRaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.DefaultRaptorParametersForPerson;
import ch.sbb.matsim.routing.pt.raptor.LeastCostRaptorRouteSelector;
import ch.sbb.matsim.routing.pt.raptor.RaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.RaptorParametersForPerson;
import ch.sbb.matsim.routing.pt.raptor.RaptorRouteSelector;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptor;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorFactory;

public class RunRouting {
	static public void main(String[] args) throws NoSuchAuthorityCodeException, FactoryException,
			MismatchedDimensionException, TransformException, IOException {
		File transitSchedulePath = new File(args[0]);
		File networkPath = new File(args[1]);
		File zonesPath = new File(args[2]);
		File trianglesPath = new File(args[3]);
		File interchangesPath = new File(args[4]);

		// Read schedule data
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);

		new TransitScheduleReader(scenario).readFile(transitSchedulePath.toString());
		new MatsimNetworkReader(scenario.getNetwork()).readFile(networkPath.toString());

		double additionalTransferTime = 30.0;
		config.transitRouter().setAdditionalTransferTime(additionalTransferTime);
		
		// ScheduleUtils.wrapSchedule(scenario.getTransitSchedule(),
		// Time.parseTime("30:00:00"));

		// Set up cost calculator
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
				scenario.getTransitSchedule(), connectionFinder, scenario.getNetwork(), walkDistanceFactor,
				0.0);

		// Set up trip
		CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem epsg2056 = CRS.decode("EPSG:2056");
		MathTransform transform = CRS.findMathTransform(wgs84, epsg2056);

		// DirectPosition originWGS84 = new DirectPosition2D(47.419117, 8.501129);
		// Hungerberg
		// DirectPosition destinationWGS84 = new DirectPosition2D(46.961060, 7.445769);
		// Bern Wankdorf

		// DirectPosition originWGS84 = new DirectPosition2D(47.419117, 8.501129);
		// DirectPosition destinationWGS84 = new DirectPosition2D(46.210630, 6.143162);

		DirectPosition originWGS84 = new DirectPosition2D(47.409311, 8.506979);
		//DirectPosition destinationWGS84 = new DirectPosition2D(47.365307, 8.546171);

		DirectPosition destinationWGS84 = new DirectPosition2D(47.412746, 9.438905);

		DirectPosition originEPSG2056 = new DirectPosition2D();
		DirectPosition destinationEPSG2056 = new DirectPosition2D();

		transform.transform(originWGS84, originEPSG2056);
		transform.transform(destinationWGS84, destinationEPSG2056);

		Coord originCoord = new Coord(originEPSG2056.getCoordinate()[0], originEPSG2056.getCoordinate()[1]);
		Coord destinationCoord = new Coord(destinationEPSG2056.getCoordinate()[0],
				destinationEPSG2056.getCoordinate()[1]);

		Link originLink = NetworkUtils.getNearestLink(scenario.getNetwork(), originCoord);
		Link destinationLink = NetworkUtils.getNearestLink(scenario.getNetwork(), destinationCoord);

		Facility<?> originFacility = new LinkWrapperFacility(originLink);
		Facility<?> destinationFacility = new LinkWrapperFacility(destinationLink);

		double departureTime = Time.parseTime("09:40:00");

		// Perform routing and cost calulcation
		List<Leg> legs = enrichedTransitRouter.calculateRoute(originFacility, destinationFacility, departureTime, null);
		List<TransitStage> transitStages = transformer.getStages(legs);

		/*
		 * List<TransitStage> transitStages = new LinkedList<>();
		 * 
		 * //transitStages.add(new TransitStage(Arrays.asList(8503000L, 8503006L),
		 * 200.0, 8.0 * 3600.0, 8.5 * 3600.0, true));
		 * 
		 * transitStages.add(new TransitStage(Arrays.asList(8503008L, 8503007L,
		 * 8503006L, 8503020L, 8503000L), 11056.718149529634, 35040.0, 35880.0, true));
		 * transitStages .add(new TransitStage(Arrays.asList(8503000L, 8507000L),
		 * 117546.05292831114, 36120.0, 39480.0, true)); transitStages.add(new
		 * TransitStage(Arrays.asList(8507000L, 8508005L, 8508006L), 28334.24109533362,
		 * 40020.0, 41160.0, true)); transitStages.add(new
		 * TransitStage(Arrays.asList(8571772L, 8589489L, 8576495L, 8588627L),
		 * 3507.123360377688, 41640.0, 41880.0, false));
		 */

		TicketSolver.Result result = transitCostCalculator.computeCost(transitStages, false);

		System.out.println("===========================");

		for (Ticket ticket : result.tickets) {
			System.out.println(ticket);
		}

		System.out.println("---------------------------");

		System.out.println(String.format("Valid: %s", result.isValid ? "yes" : "no"));
		System.out.println(String.format("Total price: %.2f CHF", result.price));

		System.out.println("===========================");

	}
}
