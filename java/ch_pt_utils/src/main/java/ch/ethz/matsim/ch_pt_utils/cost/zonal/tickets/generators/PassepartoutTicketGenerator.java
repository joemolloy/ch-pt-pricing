package ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.generators;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.ZVVStylePricing;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.ZVVStyleTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.pricing.PassepartoutPricing;

public class PassepartoutTicketGenerator extends ZVVStyleTicketGenerator {
	private PassepartoutTicketGenerator(Authority authority, Collection<Zone> doubleZones, ZVVStylePricing pricing) {
		super(authority, doubleZones, pricing);
	}

	static public PassepartoutTicketGenerator create(ZonalRegistry registry) {
		Authority authority = registry.getAuthority("Passepartout");
		List<Zone> doubleZones = Arrays.asList();

		return new PassepartoutTicketGenerator(authority, doubleZones, new PassepartoutPricing());
	}
}
