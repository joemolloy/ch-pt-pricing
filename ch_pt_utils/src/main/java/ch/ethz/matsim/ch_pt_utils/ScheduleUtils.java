package ch.ethz.matsim.ch_pt_utils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import javax.inject.Provider;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import ch.ethz.matsim.baseline_scenario.transit.connection.DefaultTransitConnectionFinder;
import ch.ethz.matsim.baseline_scenario.transit.connection.TransitConnectionFinder;
import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRouter;
import ch.ethz.matsim.baseline_scenario.transit.routing.EnrichedTransitRouter;
import ch.ethz.matsim.baseline_scenario.zurich.cutter.utils.DefaultDepartureFinder;
import ch.ethz.matsim.baseline_scenario.zurich.cutter.utils.DepartureFinder;
import ch.sbb.matsim.routing.pt.raptor.DefaultRaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.DefaultRaptorParametersForPerson;
import ch.sbb.matsim.routing.pt.raptor.LeastCostRaptorRouteSelector;
import ch.sbb.matsim.routing.pt.raptor.RaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.RaptorParametersForPerson;
import ch.sbb.matsim.routing.pt.raptor.RaptorRouteSelector;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptor;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorFactory;

public class ScheduleUtils {
	public static void wrapSchedule(TransitSchedule schedule, double latestDeparture) {
		for (TransitLine transitLine : schedule.getTransitLines().values()) {
			for (TransitRoute transitRoute : transitLine.getRoutes().values()) {
				Collection<Departure> newDepartures = new LinkedList<>();

				for (Departure departure : transitRoute.getDepartures().values()) {
					double newDepartureTime = departure.getDepartureTime();
					int day = 1;

					while (newDepartureTime <= latestDeparture) {
						if (day > 1) {
							Id<Departure> newDepartureId = Id.create(departure.getId().toString() + "_day" + day,
									Departure.class);
							Departure newDeparture = schedule.getFactory().createDeparture(newDepartureId,
									newDepartureTime);
							newDepartures.add(newDeparture);
						}

						newDepartureTime += 24.0 * 3600.0;
						day++;
					}
				}

				newDepartures.forEach(transitRoute::addDeparture);
			}
		}
	}
}
