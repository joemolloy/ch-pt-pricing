package ch.ethz.matsim.ch_pt_utils.routing.router;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.solver.TicketSolver;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStageTransformer;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.TicketGenerator;
import ch.ethz.matsim.ch_pt_utils.routing.PlanRoutingRequest;
import ch.ethz.matsim.ch_pt_utils.routing.PlanRoutingResult;
import ch.ethz.matsim.ch_pt_utils.routing.TripRoutingRequest;
import ch.ethz.matsim.ch_pt_utils.routing.TripRoutingResult;

public class DefaultRouter implements Router {
	private final Network network;
	private final EnrichedTransitRouter router;
	private final FrequencyCalculator frequencyCalculator;
	private final Collection<String> vehicleModes;
	private final Optional<TransitStageTransformer> transformer;
	private final Optional<TicketGenerator> ticketGenerator;

	public DefaultRouter(Network network, EnrichedTransitRouter router, FrequencyCalculator frequencyCalculator,
			Collection<String> vehicleModes) {
		this(network, router, frequencyCalculator, vehicleModes, Optional.empty(), Optional.empty());
	}

	public DefaultRouter(Network network, EnrichedTransitRouter router, FrequencyCalculator frequencyCalculator,
			Collection<String> vehicleModes, Optional<TransitStageTransformer> transformer,
			Optional<TicketGenerator> ticketGenerator) {
		this.network = network;
		this.router = router;
		this.frequencyCalculator = frequencyCalculator;
		this.vehicleModes = vehicleModes;
		this.transformer = transformer;
		this.ticketGenerator = ticketGenerator;
	}

	@Override
	public PlanRoutingResult process(PlanRoutingRequest planRequest) {
		List<TransitStage> planTransitStages = new LinkedList<>();
		List<TripRoutingResult> tripRoutingResults = new LinkedList<>();

		for (TripRoutingRequest tripRequest : planRequest.getTripRequests()) {
			Link originLink = NetworkUtils.getNearestLink(network, tripRequest.getOriginCoord());
			Link destinationLink = NetworkUtils.getNearestLink(network, tripRequest.getDestinationCoord());

			Facility<?> fromFacility = new LinkWrapperFacility(originLink);
			Facility<?> toFacility = new LinkWrapperFacility(destinationLink);

			double departureTime = tripRequest.getDepartureTime();

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

			boolean isTicketPriceValid = false;
			double ticketPrice = -1.0;

			// III) Calculate trip price
			if (transformer.isPresent() && ticketGenerator.isPresent()) {
				List<TransitStage> tripTransitStages = transformer.get().getStages(legs);
				planTransitStages.addAll(tripTransitStages);

				Collection<Ticket> tripTickets = ticketGenerator.get().createTickets(tripTransitStages, false);
				TicketSolver.Result ticketResult = new TicketSolver().solve(tripTransitStages.size(), tripTickets);

				isTicketPriceValid = ticketResult.isValid;
				ticketPrice = ticketResult.price;
			}

			tripRoutingResults.add(new TripRoutingResult(tripRequest.getTripId(), numberOfTransfers, isOnlyWalk,
					isTicketPriceValid, ticketPrice, inVehicleTime, inVehicleDistance, transferWalkTime,
					transferWalkDistance, initialWaitingTime, transferWaitingTime, accessEgressWalkTime,
					accessEgressWalkDistance, frequency));
		}

		boolean isTicketPriceValid = false;
		double ticketPrice = -1.0;

		// III) Calculate plan price
		if (transformer.isPresent() && ticketGenerator.isPresent()) {
			Collection<Ticket> tourTickets = ticketGenerator.get().createTickets(planTransitStages, false);
			TicketSolver.Result ticketResult = new TicketSolver().solve(planTransitStages.size(), tourTickets);

			isTicketPriceValid = ticketResult.isValid;
			ticketPrice = ticketResult.price;
		}

		return new PlanRoutingResult(planRequest.getPlanId(), tripRoutingResults, ticketPrice, isTicketPriceValid);
	}
}
