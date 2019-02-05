package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland;

import ch.ethz.matsim.ch_pt_utils.cost.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.SingleZoneTicketGenerator;

public class TransReno {
	private TransReno() {
	}

	static public ZonalTicketGenerator createTicketGenerator(ZonalRegistry zonalRegistry) {
		Authority authority = zonalRegistry.getAuthority("TransReno");
		Zone zone1 = zonalRegistry.getZone(authority, 1);
		return new SingleZoneTicketGenerator("Chur", zone1, 3.0, 2.2, 3600.0, 6.0, 4.4);
	}
}
