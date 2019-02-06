package ch.ethz.matsim.ch_pt_utils.cost.tickets;

import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;

public class FallbackTicketGenerator implements TicketGenerator {
	private final TicketGenerator delegate;

	private final double pricePerKmFullFare;
	private final double pricePerKmHalfFare;
	private final double minimumPriceFullFare;
	private final double minimumPriceHalfFare;

	public FallbackTicketGenerator(TicketGenerator delegate, double minimumPriceFullFare, double pricePerKmFullFare,
			double minimumPriceHalfFare, double pricePerKmHalfFare) {
		this.delegate = delegate;
		this.pricePerKmFullFare = pricePerKmFullFare;
		this.pricePerKmHalfFare = pricePerKmHalfFare;
		this.minimumPriceFullFare = minimumPriceFullFare;
		this.minimumPriceHalfFare = minimumPriceHalfFare;
	}

	private double calculatePrice(double distance, boolean halfFare) {
		double distanceKm = distance * 1e-3;

		if (!halfFare) {
			return Math.max(minimumPriceFullFare, pricePerKmFullFare * distanceKm);
		} else {
			return Math.max(minimumPriceHalfFare, pricePerKmHalfFare * distanceKm);
		}
	}

	@Override
	public Collection<Ticket> createTickets(List<TransitStage> stages, boolean halfFare) {
		Collection<Ticket> tickets = delegate.createTickets(stages, halfFare);

		for (int i = 0; i < stages.size(); i++) {
			int coveringTickets = 0;

			for (Ticket ticket : tickets) {
				if (ticket.getCoverage().get(i)) {
					coveringTickets++;
				}
			}

			if (coveringTickets == 0) {
				double cost = calculatePrice(stages.get(i).getDistance(), halfFare);
				cost = Math.floor(cost * 100.0 / 20.0) * 20.0 / 100.0;
				Ticket ticket = new Ticket(stages.size(), cost, "Unknown Tariff");
				ticket.getCoverage().set(i);
				tickets.add(ticket);
			}
		}

		return tickets;
	}

}
