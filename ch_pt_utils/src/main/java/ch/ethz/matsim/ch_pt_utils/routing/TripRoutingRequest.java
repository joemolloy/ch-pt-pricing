package ch.ethz.matsim.ch_pt_utils.routing;

import org.matsim.api.core.v01.Coord;

public class TripRoutingRequest {
	private final String planId;
	private final String tripId;
	private final Coord originCoord;
	private final Coord destinationCoord;
	private final double departureTime;

	public TripRoutingRequest(String planId, String tripId, Coord originCoord, Coord destinationCoord,
			double departureTime) {
		this.planId = planId;
		this.tripId = tripId;
		this.originCoord = originCoord;
		this.destinationCoord = destinationCoord;
		this.departureTime = departureTime;
	}

	public String getPlanId() {
		return planId;
	}

	public String getTripId() {
		return tripId;
	}

	public Coord getOriginCoord() {
		return originCoord;
	}

	public Coord getDestinationCoord() {
		return destinationCoord;
	}

	public double getDepartureTime() {
		return departureTime;
	}
}
