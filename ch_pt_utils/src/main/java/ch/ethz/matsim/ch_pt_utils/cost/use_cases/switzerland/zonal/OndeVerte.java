package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.DefaultZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.pricing.OndeVertePricing;

public class OndeVerte {
	private OndeVerte() {
	}

	static public ZonalTicketGenerator createTicketGenerator(ZonalRegistry zonalRegistry) {
		Authority authority = zonalRegistry.getAuthority("OndeVerte");
		return new DefaultZonalTicketGenerator(authority, new OndeVertePricing());
	}
}
