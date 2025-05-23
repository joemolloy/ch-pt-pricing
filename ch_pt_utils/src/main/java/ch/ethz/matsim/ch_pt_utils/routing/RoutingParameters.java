package ch.ethz.matsim.ch_pt_utils.routing;

import java.util.HashMap;
import java.util.Map;

/**
 * Everything in SI units! *
 */
public class RoutingParameters {
	// Walking for Raptor
	public double walkSpeed = 1.2;
	public double walkBeelineDistanceFactor = 1.3;

	public double walkBeelineConnectionDistance = 100.0;

	// Routing for Raptor
	public double minimalTransferTime = 0.0;
	public double searchRadius = 1000.0;
	public double extensionRadius = 200.0;

	// Frequency
	public double beforeDepartureOffset = 1800.0;
	public double afterDepartureOffset = 1800.0;

	// Schedule
	public double scheduleWrappingEndTime = 30.0 * 3600.0;

	// Utilities for raptor
	public Map<String, Double> utilities = new HashMap<>();

	public RoutingParameters() {
		utilities.put("waitingTime", -0.0484 / 60.0);
		utilities.put("numberOfTransfers", -0.17);

		utilities.put("accessWalkTime", -0.0804 / 60.0);
		utilities.put("egressWalkTime", -0.0804 / 60.0);
		utilities.put("transferWalkTime", -0.0484 / 60.0);
		utilities.put("directWalkTime", -0.09 / 60.0);

		utilities.put("inVehicleTime", -0.017 / 60.0);
	}
}