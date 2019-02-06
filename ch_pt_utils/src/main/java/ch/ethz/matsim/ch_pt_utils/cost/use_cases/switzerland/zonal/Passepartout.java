package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.CompositeZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.DefaultZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.SingleZoneTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.pricing.PassepartoutPricing;

public class Passepartout {
	private Passepartout() {
	}

	static public ZonalTicketGenerator createTicketGenerator(ZonalRegistry zonalRegistry) {
		Authority authority = zonalRegistry.getAuthority("Passepartout");
		Zone zone10 = zonalRegistry.getZone(authority, 10);

		CompositeZonalTicketGenerator composite = new CompositeZonalTicketGenerator();
		composite.addGenerator(new DefaultZonalTicketGenerator(authority, new PassepartoutPricing()));
		composite.addGenerator(new SingleZoneTicketGenerator("Zone 10", zone10, 4.10, 3.10, 3600.0, 8.2, 6.2));

		return composite;
	}
}
