package ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal;

import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalStage;

public interface ZonalTicketGenerator {
	Collection<Ticket> createTickets(List<ZonalStage> stages, boolean halfFare);
}
