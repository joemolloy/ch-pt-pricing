package ch.ethz.matsim.ch_pt_utils.run;

import java.util.ArrayList;
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
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;
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
import ch.ethz.matsim.baseline_scenario.transit.routing.EnrichedTransitRoute;
import ch.ethz.matsim.baseline_scenario.transit.routing.EnrichedTransitRouter;
import ch.ethz.matsim.baseline_scenario.zurich.cutter.utils.DefaultDepartureFinder;
import ch.ethz.matsim.baseline_scenario.zurich.cutter.utils.DepartureFinder;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalStage;
import ch.sbb.matsim.routing.pt.raptor.DefaultRaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.DefaultRaptorParametersForPerson;
import ch.sbb.matsim.routing.pt.raptor.LeastCostRaptorRouteSelector;
import ch.sbb.matsim.routing.pt.raptor.RaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.RaptorParametersForPerson;
import ch.sbb.matsim.routing.pt.raptor.RaptorRouteSelector;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptor;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorFactory;

public class Testing {
	static public void main(String[] args)
			throws NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException {

		/*
		 * Authority authorityZVV = new Authority("ZVV", "ZVV"); Authority
		 * authorityLibero = new Authority("Libero", "Libero");
		 * 
		 * List<Zone> zones = new LinkedList<>();
		 * 
		 * Zone zone110ZVV = new Zone(authorityZVV, 110); Zone zone120ZVV = new
		 * Zone(authorityZVV, 120); Zone zone121ZVV = new Zone(authorityZVV, 121);
		 * 
		 * zones.add(zone110ZVV); zones.add(zone120ZVV); zones.add(zone121ZVV);
		 * 
		 * Zone zone100Libero = new Zone(authorityLibero, 100); Zone zone101Libero = new
		 * Zone(authorityLibero, 101);
		 * 
		 * zones.add(zone100Libero); zones.add(zone101Libero);
		 * 
		 * ZVVTicketGenerator zvvTicketGenerator = new ZVVTicketGenerator(authorityZVV,
		 * zones); LiberoTicketGenerator liberoTicketGenerator = new
		 * LiberoTicketGenerator(authorityLibero);
		 * 
		 * CompositeTicketGenerator ticketGenerator = new CompositeTicketGenerator();
		 * ticketGenerator.addGenerator(zvvTicketGenerator);
		 * ticketGenerator.addGenerator(liberoTicketGenerator);
		 * 
		 * ZonalStage stage1 = new ZonalStage(Time.parseTime("08:30:00"),
		 * Time.parseTime("09:00:00")); stage1.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone110ZVV))); stage1.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone110ZVV)));
		 * 
		 * ZonalStage stage2 = new ZonalStage(Time.parseTime("09:00:00"),
		 * Time.parseTime("10:00:00")); stage2.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone110ZVV))); stage2.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone100Libero)));
		 * 
		 * ZonalStage stage3 = new ZonalStage(Time.parseTime("10:00:00"),
		 * Time.parseTime("10:30:00")); stage3.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone100Libero))); stage3.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone100Libero)));
		 * 
		 * ZonalStage stage4 = new ZonalStage(Time.parseTime("14:30:00"),
		 * Time.parseTime("15:00:00")); stage4.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone100Libero))); stage4.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone100Libero)));
		 * 
		 * ZonalStage stage5 = new ZonalStage(Time.parseTime("15:00:00"),
		 * Time.parseTime("16:00:00")); stage5.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone100Libero))); stage5.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone110ZVV)));
		 * 
		 * ZonalStage stage6 = new ZonalStage(Time.parseTime("16:00:00"),
		 * Time.parseTime("16:30:00")); stage6.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone110ZVV))); stage6.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone110ZVV)));
		 * 
		 * /*ZonalStage stage1 = new ZonalStage(Time.parseTime("08:30:00"),
		 * Time.parseTime("09:00:00")); stage1.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone110))); stage1.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone110)));
		 * 
		 * ZonalStage stage2 = new ZonalStage(Time.parseTime("17:00:00"),
		 * Time.parseTime("17:30:00")); stage2.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone110))); stage2.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone121)));
		 * 
		 * ZonalStage stage3 = new ZonalStage(Time.parseTime("18:30:00"),
		 * Time.parseTime("19:00:00")); stage3.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone121))); stage3.addWaypoint(new
		 * ZonalWaypoint(Collections.singleton(zone110)));
		 */

		/*
		 * List<ZonalStage> stages = Arrays.asList(stage1, stage2, stage3, stage4,
		 * stage5, stage6); Collection<Ticket> tickets =
		 * ticketGenerator.createTickets(stages);
		 * 
		 * for (int i = 0; i < stages.size(); i++) { int coveringTickets = 0;
		 * 
		 * for (Ticket ticket : tickets) { if (ticket.getCoverage().get(i)) {
		 * coveringTickets++; } }
		 * 
		 * if (coveringTickets == 0) { Ticket ticket = new Ticket(stages.size(), 100.0);
		 * ticket.getCoverage().set(i); tickets.add(ticket); } }
		 * 
		 * for (Ticket ticket : tickets) { System.out.println(ticket); }
		 */
	}

	static public void other(String[] args)
			throws NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException {
		String transitSchedulePath = args[0];
		String networkPath = args[1];

		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);

		new TransitScheduleReader(scenario).readFile(transitSchedulePath);
		new MatsimNetworkReader(scenario.getNetwork()).readFile(networkPath);

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

		double additionalTransferTime = 60.0;

		DepartureFinder departureFinder = new DefaultDepartureFinder();
		TransitConnectionFinder connectionFinder = new DefaultTransitConnectionFinder(departureFinder);

		EnrichedTransitRouter enrichedTransitRouter = new DefaultEnrichedTransitRouter(swissRailRaptor,
				scenario.getTransitSchedule(), connectionFinder, scenario.getNetwork(), walkDistanceFactor,
				additionalTransferTime);

		CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem epsg2056 = CRS.decode("EPSG:2056");
		MathTransform transform = CRS.findMathTransform(wgs84, epsg2056);

		DirectPosition originWGS84 = new DirectPosition2D(47.41619699999999682, 8.50693300000000008);
		DirectPosition destinationWGS84 = new DirectPosition2D(47.12525300000000072, 7.63778799999999958);

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

		double departureTime = Time.parseTime("09:20:00");

		List<Leg> stages = enrichedTransitRouter.calculateRoute(originFacility, destinationFacility, departureTime,
				null);

		System.out.println(originCoord);
		System.out.println(destinationCoord);

		List<ZonalStage> zonalStages = new ArrayList<>(stages.size());

		for (Leg leg : stages) {
			if (leg.getRoute() instanceof EnrichedTransitRoute) {
				EnrichedTransitRoute route = (EnrichedTransitRoute) leg.getRoute();

				int accessStopIndex = route.getAccessStopIndex();
				int egressStopIndex = route.getEgressStopIndex();

				TransitRoute transitRoute = scenario.getTransitSchedule().getTransitLines()
						.get(route.getTransitLineId()).getRoutes().get(route.getTransitRouteId());

				Departure departure = transitRoute.getDepartures().get(route.getDepartureId());

				TransitRouteStop accessStop = transitRoute.getStops().get(accessStopIndex);
				TransitRouteStop egressStop = transitRoute.getStops().get(egressStopIndex);

				TransitStopFacility accessFacility = accessStop.getStopFacility();
				TransitStopFacility egressFacility = egressStop.getStopFacility();

				double accessDepartureTime = accessStop.getDepartureOffset() + departure.getDepartureTime();
				double egressArrivalTime = egressStop.getArrivalOffset() + departure.getDepartureTime();

				System.out.println(String.format("Access: %s (%s) @ %s", accessFacility.getId().toString(),
						accessFacility.getName(), Time.writeTime(accessDepartureTime)));
				System.out.println(String.format("Egress: %s (%s) @ %s", egressFacility.getId().toString(),
						egressFacility.getName(), Time.writeTime(egressArrivalTime)));

				ZonalStage stage = new ZonalStage(accessDepartureTime, egressArrivalTime);

				/*
				 * for (int index = accessStopIndex; index <= egressStopIndex; index++) {
				 * TransitStopFacility stopFacility =
				 * transitRoute.getStops().get(index).getStopFacility(); Collection<Zone>
				 * stopZones = zoneFinder.findZones(stopFacility); ZonalWaypoint waypoint = new
				 * ZonalWaypoint(stopZones); stage.addWaypoint(waypoint); }
				 */

				zonalStages.add(stage);
			}
		}
	}
}
