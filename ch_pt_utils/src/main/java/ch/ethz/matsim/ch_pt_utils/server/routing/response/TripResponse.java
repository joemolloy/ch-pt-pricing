package ch.ethz.matsim.ch_pt_utils.server.routing.response;

import java.util.LinkedList;
import java.util.List;

public class TripResponse {
	public List<Object> stages = new LinkedList<>();
	
	public String originStreetName;
	public String destinationStreetName;
}
