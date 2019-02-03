package ch.ethz.matsim.ch_pt_utils.routing;

public class RoutingResult {
	private final String requestId;

	private final int numberOfTransfers;
	private final boolean isOnlyWalk;

	private final double singleFullFare;
	private final double singleHalfFare;

	private final double inVehicleTime;
	private final double inVehicleDistance;

	private final double transferWalkTime;
	private final double transferWalkDistance;

	private final double initialWaitingTime;
	private final double transferWaitingTime;

	private final double accessEgressWalkTime;
	private final double accessEgressWalkDistance;

	private final double frequency;

	public RoutingResult(String requestId, int numberOfTransfers, boolean isOnlyWalk, double singleFullFare,
			double singleHalfFare, double inVehicleTime, double inVehicleDistance, double transferWalkTime,
			double transferWalkDistance, double initialWaitingTime, double transferWaitingTime,
			double accessEgressWalkTime, double accessEgressWalkDistance, double frequency) {
		this.requestId = requestId;
		this.numberOfTransfers = numberOfTransfers;
		this.isOnlyWalk = isOnlyWalk;
		this.singleFullFare = singleFullFare;
		this.singleHalfFare = singleHalfFare;
		this.inVehicleTime = inVehicleTime;
		this.inVehicleDistance = inVehicleDistance;
		this.transferWalkTime = transferWalkTime;
		this.transferWalkDistance = transferWalkDistance;
		this.initialWaitingTime = initialWaitingTime;
		this.transferWaitingTime = transferWaitingTime;
		this.accessEgressWalkTime = accessEgressWalkTime;
		this.accessEgressWalkDistance = accessEgressWalkDistance;
		this.frequency = frequency;
	}

	public String getRequestId() {
		return requestId;
	}

	public int getNumberOfTransfers() {
		return numberOfTransfers;
	}

	public boolean isOnlyWalk() {
		return isOnlyWalk;
	}

	public double getSingleFullFare() {
		return singleFullFare;
	}

	public double getSingleHalfFare() {
		return singleHalfFare;
	}

	public double getInVehicleTime() {
		return inVehicleTime;
	}

	public double getInVehicleDistance() {
		return inVehicleDistance;
	}

	public double getTransferWalkTime() {
		return transferWalkTime;
	}

	public double getTransferWalkDistance() {
		return transferWalkDistance;
	}

	public double getInitialWaitingTime() {
		return initialWaitingTime;
	}

	public double getTransferWaitingTime() {
		return transferWaitingTime;
	}

	public double getAccessEgressWalkTime() {
		return accessEgressWalkTime;
	}

	public double getAccessEgressWalkDistance() {
		return accessEgressWalkDistance;
	}

	public double getFrequency() {
		return frequency;
	}
}
