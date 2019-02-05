package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland;

import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.pricing.LiberoPricing;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.DefaultZonalTicketGenerator;

public class Libero {
	private Libero() {
	}

	static public ZonalTicketGenerator createTicketGenerator(ZonalRegistry zonalRegistry) {
		Authority authority = zonalRegistry.getAuthority("Libero");
		return new DefaultZonalTicketGenerator(authority, new LiberoPricing());
	}
}
