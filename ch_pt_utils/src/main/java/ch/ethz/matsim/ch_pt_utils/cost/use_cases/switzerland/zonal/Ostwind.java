package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.DefaultZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.pricing.OstwindPricing;

public class Ostwind {
	private Ostwind() {
	}

	static public ZonalTicketGenerator createTicketGenerator(ZonalRegistry zonalRegistry) {
		Authority authority = zonalRegistry.getAuthority("Ostwind");
		return new DefaultZonalTicketGenerator(authority, new OstwindPricing());
	}
}
