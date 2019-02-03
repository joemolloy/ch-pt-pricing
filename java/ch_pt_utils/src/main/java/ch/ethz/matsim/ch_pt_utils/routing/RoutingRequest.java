package ch.ethz.matsim.ch_pt_utils.routing;

import org.matsim.api.core.v01.Coord;

public class RoutingRequest {
	private final String requestId;
	private final Coord originCoord;
	private final Coord destinationCoord;
	private final double departureTime;

	public RoutingRequest(String requestId, Coord originCoord, Coord destinationCoord, double departureTime) {
		this.requestId = requestId;
		this.originCoord = originCoord;
		this.destinationCoord = destinationCoord;
		this.departureTime = departureTime;
	}

	public String getRequestId() {
		return requestId;
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
