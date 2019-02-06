package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.pricing;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.StaticZoneTicketCalculator;

public class PassepartoutPricing extends StaticZoneTicketCalculator {
	private final static double[] SINGLE_TICKET_VALIDTY = new double[] { //
			0.0, //
			60.0, // 1 Zone
			60.0, // 2 Zones
			120.0, // 3 Zones
			120.0, // 4 Zones
			120.0, // 5 Zones
			120.0, // 6 Zones
			120.0, // 7 Zones
			180.0, // 8 Zones
			180.0, // 9 Zones
			180.0, // 10+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES = new double[] { //
			0.0, //
			3.70, // 1 Zone
			5.20, // 2 Zones
			7.80, // 3 Zones
			10.40, // 4 Zones
			13.00, // 5 Zones
			15.80, // 6 Zones
			18.40, // 7 Zones
			20.80, // 8 Zones
			23.60, // 9 Zones
			26.00, // 10+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			2.90, // 1 Zone
			3.70, // 2 Zones
			4.30, // 3 Zones
			5.20, // 4 Zones
			6.50, // 5 Zones
			7.90, // 6 Zones
			9.20, // 7 Zones
			10.40, // 8 Zones
			11.80, // 9 Zones
			13.00, // 10+ Zones
	};

	private final static double[] DAY_TICKET_PRICES = new double[] { //
			0.0, //
			7.40, // 1 Zone
			10.40, // 2 Zones
			15.60, // 3 Zones
			20.80, // 4 Zones
			26.00, // 5 Zones
			31.60, // 6 Zones
			36.80, // 7 Zones
			41.60, // 8 Zones
			47.20, // 9 Zones
			52.00, // 10+ Zones
	};

	private final static double[] DAY_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			5.80, // 1 Zone
			7.40, // 2 Zones
			8.60, // 3 Zones
			10.40, // 4 Zones
			13.00, // 5 Zones
			15.80, // 6 Zones
			18.40, // 7 Zones
			20.80, // 8 Zones
			23.60, // 9 Zones
			26.00, // 10+ Zones
	};

	public PassepartoutPricing() {
		super(SINGLE_TICKET_VALIDTY, SINGLE_TICKET_PRICES, SINGLE_TICKET_PRICES_HALFFARE, DAY_TICKET_PRICES,
				DAY_TICKET_PRICES_HALFFARE);
	}
}
