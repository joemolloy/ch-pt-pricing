package ch.ethz.matsim.ch_pt_utils.cost.stages;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitRouteStop;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import ch.ethz.matsim.baseline_scenario.transit.routing.EnrichedTransitRoute;

public class TransitStageTransformer {
	private final TransitSchedule schedule;
	private final Collection<String> railModes;

	public TransitStageTransformer(TransitSchedule schedule, Collection<String> railModes) {
		this.schedule = schedule;
		this.railModes = railModes;
	}

	public List<TransitStage> getStages(List<? extends PlanElement> elements) {
		List<TransitStage> stages = new LinkedList<>();

		for (PlanElement element : elements) {
			if (element instanceof Leg) {
				Leg leg = (Leg) element;

				if (leg.getMode().equals(TransportMode.pt)) {
					EnrichedTransitRoute route = (EnrichedTransitRoute) leg.getRoute();

					int accessStopIndex = route.getAccessStopIndex();
					int egressStopIndex = route.getEgressStopIndex();

					TransitLine transitLine = schedule.getTransitLines().get(route.getTransitLineId());
					TransitRoute transitRoute = transitLine.getRoutes().get(route.getTransitRouteId());

					Departure departure = transitRoute.getDepartures().get(route.getDepartureId());

					TransitRouteStop accessStop = transitRoute.getStops().get(accessStopIndex);
					TransitRouteStop egressStop = transitRoute.getStops().get(egressStopIndex);

					double accessDepartureTime = accessStop.getDepartureOffset() + departure.getDepartureTime();
					double egressArrivalTime = egressStop.getArrivalOffset() + departure.getDepartureTime();

					List<Long> hafasIds = new LinkedList<>();

					for (int stopIndex = accessStopIndex; stopIndex <= egressStopIndex; stopIndex++) {
						TransitRouteStop stop = transitRoute.getStops().get(stopIndex);

						long hafasStopId = Long.parseLong(stop.getStopFacility().getId().toString().split("\\.")[0]);
						hafasIds.add(hafasStopId);
					}

					TransitStage stage = new TransitStage(hafasIds, route.getDistance(), accessDepartureTime,
							egressArrivalTime, railModes.contains(transitRoute.getTransportMode()));
					stages.add(stage);
				}
			}
		}

		return stages;
	}
}
