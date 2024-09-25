package ch.ethz.matsim.ch_pt_utils.cost.stages;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.pt.routes.TransitPassengerRoute;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;

import ch.ethz.matsim.ch_pt_utils.ScheduleUtils;

public class TransitStageTransformer {
	private final TransitSchedule schedule;
	private final Collection<String> transitModes;

	public TransitStageTransformer(TransitSchedule schedule) {
		this.schedule = schedule;
		this.transitModes = ScheduleUtils.getVehicleModes(schedule);
	}

	public List<TransitStage> getStages(Collection<Leg> elements) {
		List<TransitStage> stages = new LinkedList<>();

		for (Leg leg : elements) {
			if (leg.getMode().equals(TransportMode.pt) || transitModes.contains(leg.getMode())) {
				TransitPassengerRoute route = (TransitPassengerRoute) leg.getRoute();
				TransitLine transitLine = schedule.getTransitLines().get(route.getLineId());
				TransitRoute transitRoute = transitLine.getRoutes().get(route.getRouteId());

				TransitStopFacility accessStop = schedule.getFacilities().get(route.getAccessStopId());
				TransitStopFacility egressStop = schedule.getFacilities().get(route.getEgressStopId());

				double accessDepartureTime = route.getBoardingTime().orElse(0);
				double egressArrivalTime = accessDepartureTime + route.getTravelTime().orElse(0);

				Stream<TransitStopFacility> intermediateStops = transitRoute.getStops().stream().map(x -> x.getStopFacility())
						.dropWhile(x -> (!x.equals(accessStop))).takeWhile(x -> (!x.equals(egressStop)));

				intermediateStops = Stream.concat(intermediateStops, Stream.of(egressStop));

				List<Long> hafasIds = intermediateStops.map(stop -> {
					long hafasStopId = Long.parseLong(stop.getId().toString().split("\\.")[0]);
					return hafasStopId;
				}).toList();

				TransitStage stage = new TransitStage(hafasIds, route.getDistance(), accessDepartureTime,
						egressArrivalTime, transitRoute.getTransportMode());
				stages.add(stage);
			}
		}

		return stages;
	}
}
