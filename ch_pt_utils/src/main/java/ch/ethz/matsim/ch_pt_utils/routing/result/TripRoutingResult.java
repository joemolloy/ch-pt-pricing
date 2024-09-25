package ch.ethz.matsim.ch_pt_utils.routing.result;

public class TripRoutingResult {
	private final String tripId;

	private final int numberOfTransfers;
	private final boolean isOnlyWalk;

	private final boolean isTicketPriceValid;
	private final double ticketPrice;

	private final double inTrainVehicleTime;
	private final double inTrainVehicleDistance;
	
	private final double inLocalTransitVehicleTime;
	private final double inLocalTransitVehicleDistance;

	private final double transferWalkTime;
	private final double transferWalkDistance;

	private final double initialWaitingTime;
	private final double transferWaitingTime;

	private final double accessEgressWalkTime;
	private final double accessEgressWalkDistance;

	private final double frequency;

	private boolean isTrainJourney;
	
	public TripRoutingResult(String tripId, int numberOfTransfers, boolean isOnlyWalk, boolean isTicketPriceValid,
			double ticketPrice, 
			double inTrainVehicleTime, double inTrainVehicleDistance, 
			double inLocalTransitVehicleTime, double inLocalTransitVehicleDistance, 
			double transferWalkTime,
			double transferWalkDistance, double initialWaitingTime, double transferWaitingTime,
			double accessEgressWalkTime, double accessEgressWalkDistance, double frequency, boolean isTrainJourney) {
		
		this.tripId = tripId;
		this.numberOfTransfers = numberOfTransfers;
		this.isOnlyWalk = isOnlyWalk;
		this.isTicketPriceValid = isTicketPriceValid;
		this.ticketPrice = ticketPrice;
		this.inTrainVehicleTime = inTrainVehicleTime;
		this.inTrainVehicleDistance = inTrainVehicleDistance;
		this.inLocalTransitVehicleTime = inLocalTransitVehicleTime;
		this.inLocalTransitVehicleDistance = inLocalTransitVehicleDistance;
		this.transferWalkTime = transferWalkTime;
		this.transferWalkDistance = transferWalkDistance;
		this.initialWaitingTime = initialWaitingTime;
		this.transferWaitingTime = transferWaitingTime;
		this.accessEgressWalkTime = accessEgressWalkTime;
		this.accessEgressWalkDistance = accessEgressWalkDistance;
		this.frequency = frequency;
		this.isTrainJourney = isTrainJourney;
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

	public double getTrainInVehicleTime() {
		return inTrainVehicleTime;
	}

	public double getTrainInVehicleDistance() {
		return inTrainVehicleDistance;
	}

	public double getLocalTransitInVehicleTime() {
		return inLocalTransitVehicleTime;
	}

	public double getLocalTransitInVehicleDistance() {
		return inLocalTransitVehicleDistance;
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
	
	public boolean isTrainJourney() {
		return isTrainJourney;
	}

	public double getTotalTransitTravelTime() {
		return getTrainInVehicleTime() + getLocalTransitInVehicleTime() + getTransferWalkTime() + 
				getTransferWaitingTime() + getAccessEgressWalkTime() + getInitialWaitingTime();
	}
	
	public double getTotalTransitTravelDistance() {
		return getTrainInVehicleDistance() + getLocalTransitInVehicleDistance() + 
				getTransferWalkDistance() + getAccessEgressWalkDistance();
	}

	public double getInVehicleTime() {
		return getTrainInVehicleTime() + getLocalTransitInVehicleTime();
	}
	
	public double getInVehicleDistance() {
		return getTrainInVehicleDistance() + getLocalTransitInVehicleDistance();
	}
}
