package ch.ethz.matsim.ch_pt_utils.server.routing.response;

import java.util.LinkedList;
import java.util.List;

public class TransitStageResponse {
	public final String type = "pt";

	public String originName;
	public String destinationName;
	public String lineName;
	public String transportMode;

	public double departureTime;
	public double arrivalTime;

	public List<CoordinateResponse> path = new LinkedList<>();
}
