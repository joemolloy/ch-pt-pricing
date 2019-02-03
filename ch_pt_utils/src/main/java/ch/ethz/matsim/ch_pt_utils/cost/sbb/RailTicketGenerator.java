package ch.ethz.matsim.ch_pt_utils.cost.sbb;

import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;

public interface RailTicketGenerator {
	Collection<Ticket> createTickets(List<TransitStage> stages);
}
