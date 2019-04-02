package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.pricing;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.StaticZoneTicketCalculator;

public class TVSZPricing extends StaticZoneTicketCalculator {
	private final static double[] SINGLE_TICKET_VALIDTY = new double[] { //
			0.0, //
			60.0, // 1 Zone
			60.0, // 2 Zones
			120.0, // 3 Zones
			120.0, // 4 Zones
			120.0, // 5 Zones
			120.0, // 6+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES = new double[] { //
			0.0, //
			3.70, // 1 Zone
			4.90, // 2 Zones
			6.80, // 3 Zones
			9.20, // 4 Zones
			11.30, // 5 Zones
			12.70, // 6+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			2.80, // 1 Zone
			3.20, // 2 Zones
			3.60, // 3 Zones
			4.60, // 4 Zones
			5.60, // 5 Zones
			6.40, // 6+ Zones
	};

	private final static double[] DAY_TICKET_PRICES = new double[] { //
			0.0, //
			7.40, // 1 Zone
			9.80, // 2 Zones
			13.60, // 3 Zones
			18.40, // 4 Zones
			22.60, // 5 Zones
			25.40, // 6+ Zones
	};

	private final static double[] DAY_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			5.60, // 1 Zone
			6.40, // 2 Zones
			7.20, // 3 Zones
			9.20, // 4 Zones
			11.20, // 5 Zones
			12.80, // 6+ Zones
	};

	public TVSZPricing() {
		super(SINGLE_TICKET_VALIDTY, SINGLE_TICKET_PRICES, SINGLE_TICKET_PRICES_HALFFARE, DAY_TICKET_PRICES,
				DAY_TICKET_PRICES_HALFFARE);
	}
}
