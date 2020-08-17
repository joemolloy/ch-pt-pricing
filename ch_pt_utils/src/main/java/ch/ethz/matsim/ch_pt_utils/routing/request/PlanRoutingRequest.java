package ch.ethz.matsim.ch_pt_utils.routing.request;

import java.util.List;

public class PlanRoutingRequest {
	private final String planId;
	private final List<TripRoutingRequest> tripRequests;
	private final boolean isHalfFare;

	public PlanRoutingRequest(String planId, List<TripRoutingRequest> tripRequests) {
		this.planId = planId;
		this.tripRequests = tripRequests;
		this.isHalfFare = false;
	}

	public PlanRoutingRequest(String planId, List<TripRoutingRequest> tripRequests, boolean isHalfFare) {
		this.planId = planId;
		this.tripRequests = tripRequests;
		this.isHalfFare = isHalfFare;
	}	

	public boolean isHalfFare() {
		return isHalfFare;
	}

	public String getPlanId() {
		return planId;
	}

	public List<TripRoutingRequest> getTripRequests() {
		return tripRequests;
	}
}
