package ch.ethz.matsim.ch_pt_utils.server.routing.request;

import java.util.LinkedList;
import java.util.List;

public class PlanRequest {
	public List<TripRequest> trips = new LinkedList<>();
	public boolean isHalfFare = false;
	public boolean calculateFrequency = true;
	public int frequencyWindowStart = -1;
	public int frequencyWindowEnd = -1;
}
