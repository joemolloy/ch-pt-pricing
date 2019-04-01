package ch.ethz.matsim.ch_pt_utils.routing;

import java.util.List;

public class PlanRoutingRequest {
	private final String planId;
	private final List<TripRoutingRequest> tripRequests;

	public PlanRoutingRequest(String planId, List<TripRoutingRequest> tripRequests) {
		this.planId = planId;
		this.tripRequests = tripRequests;
	}

	public String getPlanId() {
		return planId;
	}

	public List<TripRoutingRequest> getTripRequests() {
		return tripRequests;
	}
}
