package ch.ethz.matsim.ch_pt_utils.routing.result;

import java.util.List;

public class PlanRoutingResult {
	private final String planId;
	private final List<TripRoutingResult> tripResults;
	private final double ticketPrice;
	private final boolean isTicketPriceValid;

	public PlanRoutingResult(String planId, List<TripRoutingResult> tripResults, double ticketPrice,
			boolean isTicketPriceValid) {
		this.planId = planId;
		this.tripResults = tripResults;
		this.ticketPrice = ticketPrice;
		this.isTicketPriceValid = isTicketPriceValid;
	}

	public String getPlanId() {
		return planId;
	}

	public List<TripRoutingResult> getTripResults() {
		return tripResults;
	}

	public boolean isTicketPriceValid() {
		return isTicketPriceValid;
	}

	public double getTicketPrice() {
		return ticketPrice;
	}
}
