package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.pricing;

import java.util.Set;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.StaticZoneTicketCalculator;

public class ZVVPricing extends StaticZoneTicketCalculator {
	private final static double[] SINGLE_TICKET_VALIDTY = new double[] { //
			0.0, //
			60.0, // 1 Zone
			60.0, // 2 Zones
			60.0, // 3 Zones
			120.0, // 4 Zones
			120.0, // 5 Zones
			120.0, // 6 Zones
			120.0, // 7 Zones
			120.0 // 8+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES = new double[] { //
			0.0, //
			4.40, // 1 Zone
			4.40, // 2 Zones
			6.80, // 3 Zones
			8.80, // 4 Zones
			10.80, // 5 Zones
			13.00, // 6 Zones
			15.00, // 7 Zones
			17.20 // 8+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			3.10, // 1 Zone
			3.10, // 2 Zones
			3.40, // 3 Zones
			4.40, // 4 Zones
			5.40, // 5 Zones
			6.50, // 6 Zones
			7.50, // 7 Zones
			8.60 // 8+ Zones
	};

	private final static double[] DAY_TICKET_PRICES = new double[] { //
			0.0, //
			8.80, // 1 Zone
			8.80, // 2 Zones
			13.60, // 3 Zones
			17.60, // 4 Zones
			21.60, // 5 Zones
			26.00, // 6 Zones
			30.0, // 7 Zones
			34.40 // 8+ Zones
	};

	private final static double[] DAY_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			6.20, // 1 Zone
			6.20, // 2 Zones
			6.80, // 3 Zones
			8.80, // 4 Zones
			10.80, // 5 Zones
			13.00, // 6 Zones
			15.00, // 7 Zones
			17.20 // 8+ Zones
	};

	private final Zone zone110;
	private final Zone zone120;

	public ZVVPricing(Zone zone110, Zone zone120) {
		super(SINGLE_TICKET_VALIDTY, SINGLE_TICKET_PRICES, SINGLE_TICKET_PRICES_HALFFARE, DAY_TICKET_PRICES,
				DAY_TICKET_PRICES_HALFFARE);

		this.zone110 = zone110;
		this.zone120 = zone120;
	}

	@Override
	protected int calculateNumberOfZones(Set<Zone> zones) {
		int numberOfZones = zones.size();

		if (zones.contains(zone110)) {
			numberOfZones++;
		}

		if (zones.contains(zone120)) {
			numberOfZones++;
		}

		return numberOfZones;
	}
}
