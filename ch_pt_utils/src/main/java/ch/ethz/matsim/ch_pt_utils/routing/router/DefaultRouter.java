package ch.ethz.matsim.ch_pt_utils.routing.router;

import java.util.Collection;
import java.util.List;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.LinkWrapperFacility;
import org.matsim.facilities.Facility;

import ch.ethz.matsim.baseline_scenario.transit.routing.EnrichedTransitRoute;
import ch.ethz.matsim.baseline_scenario.transit.routing.EnrichedTransitRouter;
import ch.ethz.matsim.ch_pt_utils.FrequencyCalculator;
import ch.ethz.matsim.ch_pt_utils.routing.RoutingRequest;
import ch.ethz.matsim.ch_pt_utils.routing.RoutingResult;

public class DefaultRouter implements Router {
	private final Network network;
	private final EnrichedTransitRouter router;
	private final FrequencyCalculator frequencyCalculator;
	private final Collection<String> vehicleModes;
	// private final TransitStageTransformer transformer;

	public DefaultRouter(Network network, EnrichedTransitRouter router, FrequencyCalculator frequencyCalculator,
			Collection<String> vehicleModes) {
		// TransitStageTransformer transformer) {
		this.network = network;
		this.router = router;
		this.frequencyCalculator = frequencyCalculator;
		this.vehicleModes = vehicleModes;
		// this.transformer = transformer;
	}

	@Override
	public RoutingResult process(RoutingRequest request) {
		Link originLink = NetworkUtils.getNearestLink(network, request.getOriginCoord());
		Link destinationLink = NetworkUtils.getNearestLink(network, request.getDestinationCoord());

		Facility<?> fromFacility = new LinkWrapperFacility(originLink);
		Facility<?> toFacility = new LinkWrapperFacility(destinationLink);

		double departureTime = request.getDepartureTime();

		// I) Calculate trip characteristics

		List<Leg> legs = router.calculateRoute(fromFacility, toFacility, departureTime, null);

		double inVehicleTime = 0.0;
		double inVehicleDistance = 0.0;

		double initialWaitingTime = 0.0;
		double transferWaitingTime = 0.0;

		double transferWalkTime = 0.0;
		double transferWalkDistance = 0.0;

		double accessEgressWalkTime = 0.0;
		double accessEgressWalkDistance = 0.0;

		int numberOfTransfers = -1;

		boolean isInitialTransitStage = true;
		boolean isOnlyWalk = true;

		for (Leg leg : legs) {
			if (vehicleModes.contains(leg.getMode())) {
				EnrichedTransitRoute route = (EnrichedTransitRoute) leg.getRoute();

				inVehicleTime += route.getInVehicleTime();
				inVehicleDistance += route.getDistance();

				if (isInitialTransitStage) {
					isInitialTransitStage = false;
					initialWaitingTime += route.getWaitingTime();
				} else {
					transferWaitingTime += route.getWaitingTime();
				}

				numberOfTransfers++;
				isOnlyWalk = false;
			} else if (leg.getMode().equals(TransportMode.access_walk)
					|| leg.getMode().equals(TransportMode.egress_walk)) {
				accessEgressWalkTime += leg.getTravelTime();
				accessEgressWalkDistance += leg.getRoute().getDistance();
			} else if (leg.getMode().equals(TransportMode.transit_walk)) {
				transferWalkTime += leg.getTravelTime();
				transferWalkDistance += leg.getRoute().getDistance();
			} else {
				throw new IllegalStateException("Unknown transit mode found: " + leg.getMode());
			}
		}

		numberOfTransfers = Math.max(0, numberOfTransfers);

		// II) Calculate frequency
		double frequency = frequencyCalculator.calculateFrequency(fromFacility, toFacility, departureTime);

		// III) Calculate price
		// List<TransitStage> transitStages = transformer.getStages(legs);

		/*
		 * TicketSolver.Result resultHalfFare =
		 * costCalculator.computeCost(transitStages, false); double singleHalfFare =
		 * resultHalfFare.price;
		 * 
		 * TicketSolver.Result resultFullFare =
		 * costCalculator.computeCost(transitStages, false); double singleFullFare =
		 * resultFullFare.price;
		 */

		double singleHalfFare = 0.0;
		double singleFullFare = 0.0;

		return new RoutingResult(request.getRequestId(), numberOfTransfers, isOnlyWalk, singleFullFare, singleHalfFare,
				inVehicleTime, inVehicleDistance, transferWalkTime, transferWalkDistance, initialWaitingTime,
				transferWaitingTime, accessEgressWalkTime, accessEgressWalkDistance, frequency);
	}
}
