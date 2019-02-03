package ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalStage;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Zone;

public class TransRenoTicketGenerator implements ZonalTicketGenerator {
	// TODO: Missing distance-based tarif for areas outside of Chur

	private final Zone zone1;

	private TransRenoTicketGenerator(Zone zone1) {
		this.zone1 = zone1;
	}

	@Override
	public Collection<Ticket> createTickets(List<ZonalStage> stages, boolean halfFare) {
		List<Ticket> tickets = new LinkedList<>();

		Ticket dayTicket = null;

		for (int stageIndex = 0; stageIndex < stages.size(); stageIndex++) {
			ZonalStage stage = stages.get(stageIndex);

			if (stage.isCovered(Collections.singleton(zone1))) {
				double singlePrice = halfFare ? 2.20 : 3.0;
				double dayPrice = halfFare ? 4.40 : 6.0;

				if (dayTicket == null) {
					dayTicket = new Ticket(stages.size(), dayPrice, "Chur Day");
				}

				Ticket singleTicket = new Ticket(stages.size(), singlePrice, "Chur Single");
				dayTicket.getCoverage().set(stageIndex);

				for (int futureIndex = stageIndex; futureIndex < stages.size(); futureIndex++) {
					ZonalStage futureStage = stages.get(futureIndex);
					double maximumValidity = futureStage.getDepartureTime() + 3600.0;

					if (futureStage.isCovered(Collections.singleton(zone1)) && futureStage.isCovered(maximumValidity)) {
						singleTicket.getCoverage().set(futureIndex);
					}
				}

				tickets.add(singleTicket);
			}
		}

		if (dayTicket != null) {
			tickets.add(dayTicket);
		}

		return tickets;
	}

	static public TransRenoTicketGenerator create(ZonalRegistry registry) {
		Authority authority = registry.getAuthority("TransReno");
		return new TransRenoTicketGenerator(registry.getZone(authority, 1));
	}
}
