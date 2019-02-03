package ch.ethz.matsim.ch_pt_utils.routing;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.inject.Provider;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlansCalcRouteConfigGroup;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import ch.ethz.matsim.baseline_scenario.transit.connection.DefaultTransitConnectionFinder;
import ch.ethz.matsim.baseline_scenario.transit.connection.TransitConnectionFinder;
import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRouter;
import ch.ethz.matsim.baseline_scenario.transit.routing.EnrichedTransitRouter;
import ch.ethz.matsim.baseline_scenario.zurich.cutter.utils.DefaultDepartureFinder;
import ch.ethz.matsim.baseline_scenario.zurich.cutter.utils.DepartureFinder;
import ch.ethz.matsim.ch_pt_utils.FrequencyCalculator;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStageTransformer;
import ch.sbb.matsim.routing.pt.raptor.DefaultRaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.DefaultRaptorParametersForPerson;
import ch.sbb.matsim.routing.pt.raptor.LeastCostRaptorRouteSelector;
import ch.sbb.matsim.routing.pt.raptor.RaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.RaptorParametersForPerson;
import ch.sbb.matsim.routing.pt.raptor.RaptorRouteSelector;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptor;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorFactory;

public class RoutingToolbox {
	private final Config config;
	private final Scenario scenario;

	private final TransitSchedule schedule;
	private final Network network;

	private Optional<SwissRailRaptor> swissRailRaptor = Optional.empty();
	private Optional<EnrichedTransitRouter> enrichedTransitRouter = Optional.empty();
	private Optional<FrequencyCalculator> frequencyCalculator = Optional.empty();
	private Optional<TransitStageTransformer> transitStageTransformer = Optional.empty();

	private Parameters parameters;

	static public class Parameters {
		public double beforeDepartureOffset = 1800.0;
		public double afterDepartureOffset = 1800.0;

		public double additionalTransferTime = 0.0;
		public double walkDistanceFactor = 1.3;

		public Collection<String> railModes = Arrays.asList("rail");
		
		public double scheduleWrappingEndTime = 30.0 * 3600.0;
	}

	public RoutingToolbox(Parameters parameters, Network network, TransitSchedule schedule) {
		this.network = network;
		this.schedule = schedule;
		this.parameters = parameters;

		this.config = ConfigUtils.createConfig();
		this.scenario = ScenarioUtils.createScenario(config);

		config.transitRouter().setAdditionalTransferTime(parameters.additionalTransferTime);

		try {
			Field field = PlansCalcRouteConfigGroup.class.getDeclaredField("acceptModeParamsWithoutClearing");
			field.setAccessible(true);
			field.setBoolean(config.plansCalcRoute(), true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		config.plansCalcRoute().getOrCreateModeRoutingParams(TransportMode.walk)
				.setBeelineDistanceFactor(parameters.walkDistanceFactor);
		config.plansCalcRoute().getOrCreateModeRoutingParams(TransportMode.transit_walk)
				.setBeelineDistanceFactor(parameters.walkDistanceFactor);
		config.plansCalcRoute().getOrCreateModeRoutingParams(TransportMode.access_walk)
				.setBeelineDistanceFactor(parameters.walkDistanceFactor);
		config.plansCalcRoute().getOrCreateModeRoutingParams(TransportMode.egress_walk)
				.setBeelineDistanceFactor(parameters.walkDistanceFactor);
	}

	public SwissRailRaptor getSwissRailRaptor() {
		if (!swissRailRaptor.isPresent()) {
			RaptorParametersForPerson raptorParametersForPerson = new DefaultRaptorParametersForPerson(config);
			RaptorIntermodalAccessEgress raptorIntermodalAccessEgress = new DefaultRaptorIntermodalAccessEgress();
			RaptorRouteSelector raptorRouteSelector = new LeastCostRaptorRouteSelector();
			Map<String, Provider<RoutingModule>> routingModuleProviders = Collections.emptyMap();

			SwissRailRaptorFactory factory = new SwissRailRaptorFactory(schedule, config, network,
					raptorParametersForPerson, raptorRouteSelector, raptorIntermodalAccessEgress, config.plans(),
					scenario.getPopulation(), routingModuleProviders);

			swissRailRaptor = Optional.of(factory.get());
		}

		return swissRailRaptor.get();
	}

	public EnrichedTransitRouter getEnrichedTransitRouter() {
		if (!enrichedTransitRouter.isPresent()) {
			DepartureFinder departureFinder = new DefaultDepartureFinder();
			TransitConnectionFinder connectionFinder = new DefaultTransitConnectionFinder(departureFinder);

			enrichedTransitRouter = Optional.of(new DefaultEnrichedTransitRouter(getSwissRailRaptor(), schedule,
					connectionFinder, network, parameters.walkDistanceFactor, parameters.additionalTransferTime));
		}

		return enrichedTransitRouter.get();
	}

	public FrequencyCalculator getFrequencyCalculator() {
		if (!frequencyCalculator.isPresent()) {
			frequencyCalculator = Optional.of(new FrequencyCalculator(getSwissRailRaptor(),
					parameters.beforeDepartureOffset, parameters.afterDepartureOffset));
		}

		return frequencyCalculator.get();
	}

	public TransitStageTransformer getTransitStageTransformer() {
		if (!transitStageTransformer.isPresent()) {
			transitStageTransformer = Optional.of(new TransitStageTransformer(schedule, parameters.railModes));
		}

		return transitStageTransformer.get();
	}
}
