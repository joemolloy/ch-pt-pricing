package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland;

import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.pricing.UniresoPricing;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.CompositeZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.DefaultZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.SingleZoneTicketGenerator;

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
