package ch.ethz.matsim.ch_pt_utils.run;

import java.util.Collections;
import java.util.Map;

import javax.inject.Provider;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.router.RoutingModule;
import org.matsim.core.scenario.ScenarioUtils;
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

public class RoutingUtils {
	static public EnrichedTransitRouter createEnrichedTransitRouter(Network network, TransitSchedule schedule) {
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);

		double walkDistanceFactor = config.plansCalcRoute().getOrCreateModeRoutingParams("walk")
				.getBeelineDistanceFactor();

		RaptorParametersForPerson raptorParametersForPerson = new DefaultRaptorParametersForPerson(config);
		RaptorIntermodalAccessEgress raptorIntermodalAccessEgress = new DefaultRaptorIntermodalAccessEgress();
		RaptorRouteSelector raptorRouteSelector = new LeastCostRaptorRouteSelector();
		Map<String, Provider<RoutingModule>> routingModuleProviders = Collections.emptyMap();

		SwissRailRaptorFactory factory = new SwissRailRaptorFactory(schedule, config, network,
				raptorParametersForPerson, raptorRouteSelector, raptorIntermodalAccessEgress, config.plans(),
				scenario.getPopulation(), routingModuleProviders);

		SwissRailRaptor swissRailRaptor = factory.get();

		double additionalTransferTime = 60.0;

		DepartureFinder departureFinder = new DefaultDepartureFinder();
		TransitConnectionFinder connectionFinder = new DefaultTransitConnectionFinder(departureFinder);

		return new DefaultEnrichedTransitRouter(swissRailRaptor, scenario.getTransitSchedule(), connectionFinder,
				scenario.getNetwork(), walkDistanceFactor, additionalTransferTime);
	}
}
