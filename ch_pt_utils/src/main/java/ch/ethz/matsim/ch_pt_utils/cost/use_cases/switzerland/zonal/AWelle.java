package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.DefaultZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.pricing.AWellePricing;

public class AWelle {
	private AWelle() {
	}

	static public ZonalTicketGenerator createTicketGenerator(ZonalRegistry zonalRegistry) {
		Authority authority = zonalRegistry.getAuthority("AWelle");
		return new DefaultZonalTicketGenerator(authority, new AWellePricing());
	}
}
