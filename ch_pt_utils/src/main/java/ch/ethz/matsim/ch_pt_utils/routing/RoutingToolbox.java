package ch.ethz.matsim.ch_pt_utils.routing;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Person;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

import ch.ethz.matsim.baseline_scenario.transit.connection.DefaultTransitConnectionFinder;
import ch.ethz.matsim.baseline_scenario.transit.connection.TransitConnectionFinder;
import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRouter;
import ch.ethz.matsim.baseline_scenario.transit.routing.EnrichedTransitRouter;
import ch.ethz.matsim.baseline_scenario.zurich.cutter.utils.DefaultDepartureFinder;
import ch.ethz.matsim.baseline_scenario.zurich.cutter.utils.DepartureFinder;
import ch.ethz.matsim.ch_pt_utils.FrequencyCalculator;
import ch.ethz.matsim.ch_pt_utils.ScheduleUtils;
import ch.sbb.matsim.config.SwissRailRaptorConfigGroup;
import ch.sbb.matsim.routing.pt.raptor.DefaultRaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.LeastCostRaptorRouteSelector;
import ch.sbb.matsim.routing.pt.raptor.RaptorIntermodalAccessEgress;
import ch.sbb.matsim.routing.pt.raptor.RaptorParameters;
import ch.sbb.matsim.routing.pt.raptor.RaptorParametersForPerson;
import ch.sbb.matsim.routing.pt.raptor.RaptorRouteSelector;
import ch.sbb.matsim.routing.pt.raptor.RaptorStaticConfig;
import ch.sbb.matsim.routing.pt.raptor.RaptorStaticConfig.RaptorOptimization;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptor;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorData;

public class RoutingToolbox {
	private final static Logger logger = Logger.getLogger(RoutingToolbox.class);

	private final SwissRailRaptorData raptorData;
	private final RaptorStaticConfig raptorStaticConfig;
	private final RaptorParameters raptorParameters;

	private final TransitSchedule schedule;
	private final Network network;

	private Optional<SwissRailRaptor> swissRailRaptor = Optional.empty();
	private Optional<EnrichedTransitRouter> enrichedTransitRouter = Optional.empty();
	private Optional<FrequencyCalculator> frequencyCalculator = Optional.empty();

	private final RoutingParameters parameters;
	private final Collection<String> vehicleModes;

	private double getWalkTimeUtility(RoutingParameters parameters) {
		if (!parameters.utilities.containsKey("walkTime")) {
			throw new IllegalStateException("Utility for walkTime not set.");
		}

		return parameters.utilities.get("walkTime");
	}

	private double getInVehicleTimeUtility(RoutingParameters parameters) {
		if (!parameters.utilities.containsKey("inVehicleTime")) {
			throw new IllegalStateException("Utility for inVehicleTime not set.");
		}

		return parameters.utilities.get("inVehicleTime");
	}

	public RoutingToolbox(RoutingParameters parameters, Network network, TransitSchedule schedule) {
		this.network = network;
		this.schedule = schedule;
		this.parameters = parameters;

		for (String prefix : Arrays.asList("access", "egress", "transfer", "direct")) {
			String utilityName = prefix + "WalkTime";

			if (!parameters.utilities.containsKey(utilityName)) {
				logger.info("Filling in " + utilityName + " utility by walkTime");
				parameters.utilities.put(utilityName, getWalkTimeUtility(parameters));
			}
		}

		vehicleModes = ScheduleUtils.getVehicleModes(schedule);

		for (String prefix : vehicleModes) {
			String utilityName = prefix + "InVehicleTime";

			if (!parameters.utilities.containsKey(utilityName)) {
				logger.info("Filling in " + utilityName + " utility by inVehicleTime");
				parameters.utilities.put(utilityName, getInVehicleTimeUtility(parameters));
			}
		}

		if (!parameters.utilities.containsKey("numberOfTransfers")) {
			throw new IllegalStateException("Utility for numberOfTransfers not set.");
		}

		if (!parameters.utilities.containsKey("waitingTime")) {
			throw new IllegalStateException("Utility for waitingTime not set.");
		}

		raptorStaticConfig = new RaptorStaticConfig();
		raptorStaticConfig.setBeelineWalkSpeed(parameters.walkSpeed / parameters.walkBeelineDistanceFactor);
		raptorStaticConfig.setBeelineWalkConnectionDistance(parameters.walkBeelineConnectionDistance);

		raptorStaticConfig.setMarginalUtilityOfTravelTimeAccessWalk_utl_s(parameters.utilities.get("accessWalkTime"));
		raptorStaticConfig.setMarginalUtilityOfTravelTimeEgressWalk_utl_s(parameters.utilities.get("egressWalkTime"));
		raptorStaticConfig.setMarginalUtilityOfTravelTimeWalk_utl_s(parameters.utilities.get("transferWalkTime"));

		raptorStaticConfig.setMinimalTransferTime(parameters.minimalTransferTime);
		raptorStaticConfig.setOptimization(RaptorOptimization.OneToOneRouting);
		raptorStaticConfig.setUseModeMappingForPassengers(true);

		for (String mode : vehicleModes) {
			raptorStaticConfig.addModeMappingForPassengers(mode, mode);
		}

		raptorParameters = new RaptorParameters(new SwissRailRaptorConfigGroup());
		raptorParameters.setBeelineWalkSpeed(parameters.walkSpeed / parameters.walkBeelineDistanceFactor);
		raptorParameters.setExtensionRadius(parameters.extensionRadius);
		raptorParameters.setSearchRadius(parameters.searchRadius);

		raptorParameters.setTransferPenaltyTravelTimeToCostFactor(0.0);
		raptorParameters.setTransferPenaltyFixCostPerTransfer(-parameters.utilities.get("numberOfTransfers"));
		raptorParameters.setMarginalUtilityOfWaitingPt_utl_s(parameters.utilities.get("waitingTime"));

		for (String mode : vehicleModes) {
			String utilityName = mode + "InVehicleTime";
			raptorParameters.setMarginalUtilityOfTravelTime_utl_s(mode, parameters.utilities.get(utilityName));
		}

		this.raptorData = SwissRailRaptorData.create(schedule, raptorStaticConfig, network);
	}

	public SwissRailRaptor getSwissRailRaptor() {
		if (!swissRailRaptor.isPresent()) {
			RaptorParametersForPerson raptorParametersForPerson = (Person person) -> {
				return raptorParameters;
			};

			RaptorIntermodalAccessEgress raptorIntermodalAccessEgress = new DefaultRaptorIntermodalAccessEgress();
			RaptorRouteSelector raptorRouteSelector = new LeastCostRaptorRouteSelector();

			SwissRailRaptor raptor = new SwissRailRaptor(raptorData, raptorParametersForPerson, raptorRouteSelector,
					raptorIntermodalAccessEgress);
			swissRailRaptor = Optional.of(raptor);
		}

		return swissRailRaptor.get();
	}

	public EnrichedTransitRouter getEnrichedTransitRouter() {
		if (!enrichedTransitRouter.isPresent()) {
			DepartureFinder departureFinder = new DefaultDepartureFinder();
			TransitConnectionFinder connectionFinder = new DefaultTransitConnectionFinder(departureFinder);

			enrichedTransitRouter = Optional.of(new DefaultEnrichedTransitRouter(getSwissRailRaptor(), schedule,
					connectionFinder, network, parameters.walkBeelineDistanceFactor, 0.0, vehicleModes));
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
}
