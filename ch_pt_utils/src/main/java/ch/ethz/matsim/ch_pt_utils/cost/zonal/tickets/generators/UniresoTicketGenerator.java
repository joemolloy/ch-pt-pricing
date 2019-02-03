package ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalStage;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.ZVVStylePricing;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.ZVVStyleTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.pricing.UniresoPricing;

public class UniresoTicketGenerator extends ZVVStyleTicketGenerator {
	private final Zone toutGeneveZone;

	private UniresoTicketGenerator(Authority authority, Zone toutGeneveZone, ZVVStylePricing pricing) {
		super(authority, Collections.emptyList(), pricing);
		this.toutGeneveZone = toutGeneveZone;
	}

	@Override
	public Collection<Ticket> createTickets(List<ZonalStage> stages, boolean halfFare) {
		Collection<Ticket> tickets = super.createTickets(stages, halfFare);

		boolean addToutGeneve = false;

		for (ZonalStage stage : stages) {
			if (stage.isCovered(Collections.singleton(toutGeneveZone))) {
				addToutGeneve = true;
				break;
			}
		}

		if (addToutGeneve) {
			double singlePrice = halfFare ? 2.00 : 3.00;
			double dayPrice = halfFare ? 7.30 : 10.00;

			Ticket dayTicket = new Ticket(stages.size(), dayPrice, "Tout Geneve Day");

			for (int stageIndex = 0; stageIndex < stages.size(); stageIndex++) {
				Ticket singleTicket = new Ticket(stages.size(), singlePrice, "Tout Geneve Single");
				double maximumValidity = stages.get(stageIndex).getDepartureTime() + 3600.0;

				for (int futureIndex = stageIndex; futureIndex < stages.size(); futureIndex++) {
					ZonalStage futureStage = stages.get(futureIndex);

					if (futureStage.isCovered(Collections.singleton(toutGeneveZone))
							&& futureStage.isCovered(maximumValidity)) {
						singleTicket.getCoverage().set(futureIndex);
					}
				}

				tickets.add(singleTicket);

				if (stages.get(stageIndex).isCovered(Collections.singleton(toutGeneveZone))) {
					dayTicket.getCoverage().set(stageIndex);
				}
			}

			tickets.add(dayTicket);
		}

		return tickets;
	}

	static public UniresoTicketGenerator create(ZonalRegistry registry) {
		Authority authority = registry.getAuthority("Unireso");
		Zone toutGeneveZone = registry.getZone(authority, 10);

		return new UniresoTicketGenerator(authority, toutGeneveZone, new UniresoPricing());
	}
}
