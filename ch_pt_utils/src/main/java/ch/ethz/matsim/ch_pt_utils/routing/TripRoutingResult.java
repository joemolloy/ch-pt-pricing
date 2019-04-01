package ch.ethz.matsim.ch_pt_utils.routing;

public class TripRoutingResult {
	private final String tripId;

	private final int numberOfTransfers;
	private final boolean isOnlyWalk;

	private final boolean isTicketPriceValid;
	private final double ticketPrice;

	private final double inVehicleTime;
	private final double inVehicleDistance;

	private final double transferWalkTime;
	private final double transferWalkDistance;

	private final double initialWaitingTime;
	private final double transferWaitingTime;

	private final double accessEgressWalkTime;
	private final double accessEgressWalkDistance;

	private final double frequency;

	public TripRoutingResult(String tripId, int numberOfTransfers, boolean isOnlyWalk, boolean isTicketPriceValid,
			double ticketPrice, double inVehicleTime, double inVehicleDistance, double transferWalkTime,
			double transferWalkDistance, double initialWaitingTime, double transferWaitingTime,
			double accessEgressWalkTime, double accessEgressWalkDistance, double frequency) {
		this.tripId = tripId;
		this.numberOfTransfers = numberOfTransfers;
		this.isOnlyWalk = isOnlyWalk;
		this.isTicketPriceValid = isTicketPriceValid;
		this.ticketPrice = ticketPrice;
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

	public String getTripId() {
		return tripId;
	}

	public int getNumberOfTransfers() {
		return numberOfTransfers;
	}

	public boolean isOnlyWalk() {
		return isOnlyWalk;
	}

	public boolean isTicketPriceValid() {
		return isTicketPriceValid;
	}

	public double getTicketPrice() {
		return ticketPrice;
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
