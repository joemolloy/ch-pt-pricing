package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland;

import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.pricing.EngadinMobilPricing;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.DefaultZonalTicketGenerator;

public class EngadinMobil {
	private EngadinMobil() {
	}

	static public ZonalTicketGenerator createTicketGenerator(ZonalRegistry zonalRegistry) {
		Authority authority = zonalRegistry.getAuthority("EngadinMobil");
		return new DefaultZonalTicketGenerator(authority, new EngadinMobilPricing());
	}
}
