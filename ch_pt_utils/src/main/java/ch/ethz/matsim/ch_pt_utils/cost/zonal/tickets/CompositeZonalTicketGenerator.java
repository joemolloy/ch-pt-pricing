package ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalStage;

public class CompositeZonalTicketGenerator implements ZonalTicketGenerator {
	private final List<ZonalTicketGenerator> generators = new LinkedList<>();

	public void addGenerator(ZonalTicketGenerator generator) {
		this.generators.add(generator);
	}

	@Override
	public Collection<Ticket> createTickets(List<ZonalStage> stages, boolean halfFare) {
		List<Ticket> tickets = new LinkedList<>();

		for (ZonalTicketGenerator generator : generators) {
			tickets.addAll(generator.createTickets(stages, halfFare));
		}

		return tickets;
	}

}
