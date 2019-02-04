package ch.ethz.matsim.ch_pt_utils.routing.router;

import java.util.Collection;

import org.matsim.api.core.v01.network.Network;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import ch.ethz.matsim.baseline_scenario.transit.routing.EnrichedTransitRouter;
import ch.ethz.matsim.ch_pt_utils.FrequencyCalculator;
import ch.ethz.matsim.ch_pt_utils.ScheduleUtils;
import ch.ethz.matsim.ch_pt_utils.routing.RoutingParameters;
import ch.ethz.matsim.ch_pt_utils.routing.RoutingToolbox;

public class DefaultRouterFactory implements RouterFactory {
	private final RoutingParameters parameters;
	private final Network network;
	private final TransitSchedule schedule;
	private final Collection<String> vehicleModes;

	public DefaultRouterFactory(RoutingParameters parameters, Network network, TransitSchedule schedule) {
		this.parameters = parameters;
		this.network = network;
		this.schedule = schedule;
		this.vehicleModes = ScheduleUtils.getVehicleModes(schedule);
	}

	@Override
	public Router createRouter() {
		RoutingToolbox toolbox = new RoutingToolbox(parameters, network, schedule);

		EnrichedTransitRouter router = toolbox.getEnrichedTransitRouter();
		FrequencyCalculator frequencyCalculator = toolbox.getFrequencyCalculator();
		// TransitStageTransformer transformer = toolbox.getTransitStageTransformer();

		return new DefaultRouter(network, router, frequencyCalculator, vehicleModes); // , transformer);
	}
}
