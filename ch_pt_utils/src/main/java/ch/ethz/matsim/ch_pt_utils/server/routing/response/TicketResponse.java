package ch.ethz.matsim.ch_pt_utils.server.routing.response;

import java.util.LinkedList;
import java.util.List;

public class TicketResponse {
	public String description;
	public List<Boolean> coverage = new LinkedList<>();
	public double price;
}
