package ch.ethz.matsim.ch_pt_utils.cost.tickets;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;

public class CompositeTicketGenerator implements TicketGenerator {
	private final Collection<TicketGenerator> generators = new LinkedList<>();

	public void addGenerator(TicketGenerator generator) {
		generators.add(generator);
	}

	@Override
	public Collection<Ticket> createTickets(List<TransitStage> stages, boolean halfFare) {
		List<Ticket> tickets = new LinkedList<>();

		for (TicketGenerator generator : generators) {
			tickets.addAll(generator.createTickets(stages, halfFare));
		}

		return tickets;
	}

}
