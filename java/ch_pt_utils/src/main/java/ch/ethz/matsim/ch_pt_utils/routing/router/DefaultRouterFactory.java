package ch.ethz.matsim.ch_pt_utils.routing.router;

import org.matsim.api.core.v01.network.Network;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import ch.ethz.matsim.baseline_scenario.transit.routing.EnrichedTransitRouter;
import ch.ethz.matsim.ch_pt_utils.FrequencyCalculator;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStageTransformer;
import ch.ethz.matsim.ch_pt_utils.routing.RoutingToolbox;
import ch.ethz.matsim.ch_pt_utils.routing.RoutingToolbox.Parameters;

public class DefaultRouterFactory implements RouterFactory {
	private final Parameters parameters;
	private final Network network;
	private final TransitSchedule schedule;

	public DefaultRouterFactory(Parameters parameters, Network network, TransitSchedule schedule) {
		this.parameters = parameters;
		this.network = network;
		this.schedule = schedule;
	}

	@Override
	public Router createRouter() {
		RoutingToolbox toolbox = new RoutingToolbox(parameters, network, schedule);

		EnrichedTransitRouter router = toolbox.getEnrichedTransitRouter();
		FrequencyCalculator frequencyCalculator = toolbox.getFrequencyCalculator();
		TransitStageTransformer transformer = toolbox.getTransitStageTransformer();

		return new DefaultRouter(network, router, frequencyCalculator, transformer);
	}
}
