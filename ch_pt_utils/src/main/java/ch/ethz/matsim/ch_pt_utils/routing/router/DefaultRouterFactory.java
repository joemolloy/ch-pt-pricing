package ch.ethz.matsim.ch_pt_utils.routing.router;

import java.util.Collection;
import java.util.Optional;

import org.matsim.api.core.v01.network.Network;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import ch.ethz.matsim.baseline_scenario.transit.routing.EnrichedTransitRouter;
import ch.ethz.matsim.ch_pt_utils.FrequencyCalculator;
import ch.ethz.matsim.ch_pt_utils.ScheduleUtils;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStageTransformer;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.TicketGenerator;
import ch.ethz.matsim.ch_pt_utils.routing.RoutingParameters;
import ch.ethz.matsim.ch_pt_utils.routing.RoutingToolbox;

public class DefaultRouterFactory implements RouterFactory {
	private final RoutingParameters parameters;
	private final Network network;
	private final TransitSchedule schedule;
	private final Collection<String> vehicleModes;
	private final Optional<TransitStageTransformer> transitStageTransformer;
	private final Optional<TicketGenerator> ticketGenerator;

	public DefaultRouterFactory(RoutingParameters parameters, Network network, TransitSchedule schedule) {
		this(parameters, network, schedule, Optional.empty(), Optional.empty());
	}

	public DefaultRouterFactory(RoutingParameters parameters, Network network, TransitSchedule schedule,
			Optional<TransitStageTransformer> transitStageTransformer, Optional<TicketGenerator> ticketGenerator) {
		this.parameters = parameters;
		this.network = network;
		this.schedule = schedule;
		this.vehicleModes = ScheduleUtils.getVehicleModes(schedule);
		this.transitStageTransformer = transitStageTransformer;
		this.ticketGenerator = ticketGenerator;
	}

	@Override
	public Router createRouter() {
		RoutingToolbox toolbox = new RoutingToolbox(parameters, network, schedule);

		EnrichedTransitRouter router = toolbox.getEnrichedTransitRouter();
		FrequencyCalculator frequencyCalculator = toolbox.getFrequencyCalculator();

		return new DefaultRouter(network, router, frequencyCalculator, vehicleModes, transitStageTransformer,
				ticketGenerator);
	}
}
