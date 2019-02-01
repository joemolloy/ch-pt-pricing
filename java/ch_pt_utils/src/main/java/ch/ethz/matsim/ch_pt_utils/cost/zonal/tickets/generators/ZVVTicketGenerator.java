package ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.ZVVStylePricing;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.ZVVStyleTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.pricing.ZVVPricing;

public class ZVVTicketGenerator extends ZVVStyleTicketGenerator {
	private ZVVTicketGenerator(Authority authority, Collection<Zone> doubleZones, ZVVStylePricing pricing) {
		super(authority, doubleZones, pricing);
	}

	static public ZVVTicketGenerator create(ZonalRegistry registry) {
		Authority authority = registry.getAuthority("ZVV");
		List<Zone> doubleZones = Arrays.asList(registry.getZone(authority, 110), registry.getZone(authority, 120));

		return new ZVVTicketGenerator(authority, doubleZones, new ZVVPricing());
	}
}
