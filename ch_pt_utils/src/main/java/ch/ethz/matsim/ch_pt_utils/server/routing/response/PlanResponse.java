package ch.ethz.matsim.ch_pt_utils.server.routing.response;

import java.util.LinkedList;
import java.util.List;

public class PlanResponse {
	public List<TripResponse> trips = new LinkedList<>();
	public List<TicketResponse> tickets = new LinkedList<>();
	
	public double totalPrice;
	
	public String error;
}
