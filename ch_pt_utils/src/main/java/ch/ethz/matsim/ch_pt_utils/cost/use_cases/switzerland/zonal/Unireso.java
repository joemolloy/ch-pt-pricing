package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.CompositeZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.DefaultZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.SingleZoneTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.pricing.UniresoPricing;

public class Unireso {
	private Unireso() {
	}

	static public ZonalTicketGenerator createTicketGenerator(ZonalRegistry zonalRegistry) {
		Authority authority = zonalRegistry.getAuthority("Unireso");
		Zone toutGeneveZone = zonalRegistry.getZone(authority, 10);

		CompositeZonalTicketGenerator composite = new CompositeZonalTicketGenerator();
		composite.addGenerator(new DefaultZonalTicketGenerator(authority, new UniresoPricing()));
		composite.addGenerator(
				new SingleZoneTicketGenerator("Tout Geneve", toutGeneveZone, 3.0, 2.0, 3600.0, 10.0, 7.3));

		return composite;
	}
}
