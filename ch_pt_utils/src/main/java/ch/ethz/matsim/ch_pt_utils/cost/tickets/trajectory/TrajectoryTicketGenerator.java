package ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory;

import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;

public interface TrajectoryTicketGenerator {
	Collection<Ticket> createTickets(List<TransitStage> trajectory, int startIndex, int numberOfStages, boolean halfFare);
}
