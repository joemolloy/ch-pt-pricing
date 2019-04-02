package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland;

import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.TicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb.DistanceTariff;

public class DistanceTicketGenerator implements TicketGenerator {
	private final TicketGenerator delegate;

	public DistanceTicketGenerator(TicketGenerator delegate) {
		this.delegate = delegate;
	}

	private double calculatePrice(double distance, boolean halfFare) {
		double distanceKm = distance * 1e-3;

		if (!halfFare) {
			return DistanceTariff.calculateCost(distanceKm);
		} else {
			return 0.5 * DistanceTariff.calculateCost(distanceKm);
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
				Ticket ticket = new Ticket(stages.size(), cost, "Generic Distance");
				ticket.getCoverage().set(i);
				tickets.add(ticket);
			}
		}

		return tickets;
	}

}
