package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.pricing;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.StaticZoneTicketCalculator;

public class OndeVertePricing extends StaticZoneTicketCalculator {
	private final static double[] SINGLE_TICKET_VALIDTY = new double[] { //
			0.0, //
			60.0, // 1 Zone
			60.0, // 2 Zones
			90.0, // 3 Zones
			120.0, // 4 Zones
			120.0, // 5 Zones
			120.0, // 6 Zones
			120.0, // 7 Zones
			180.0, // 8 Zones
			180.0, // 9 Zones
			180.0 // 10+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES = new double[] { //
			0.0, //
			4.20, // 1 Zones
			4.20, // 2 Zones
			6.20, // 3 Zones
			8.20, // 4 Zones
			10.40, // 5 Zones
			12.40, // 6 Zones
			14.40, // 7 Zones
			16.40, // 8 Zones
			18.60, // 9 Zones
			20.60 // 10+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			2.20, // 1 Zone
			2.20, // 2 Zones
			3.10, // 3 Zones
			4.10, // 4 Zones
			5.20, // 5 Zones
			6.20, // 6 Zones
			7.20, // 7 Zones
			8.20, // 8 Zones
			9.30, // 9 Zones
			10.30 // 10+ Zones
	};

	private final static double[] DAY_TICKET_PRICES = new double[] { //
			0.0, //
			12.60, // 1 Zone
			12.60, // 2 Zones
			14.40, // 3 Zones
			16.40, // 4 Zones
			20.80, // 5 Zones
			24.80, // 6 Zones
			28.80, // 7 Zones
			32.80, // 8 Zones
			37.20, // 9 Zones
			41.20 // 10+ Zones
	};

	private final static double[] DAY_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			6.60, // 1 Zone
			6.60, // 2 Zones
			7.20, // 3 Zones
			8.20, // 4 Zones
			10.40, // 5 Zones
			12.40, // 6 Zones
			14.40, // 7 Zones
			16.40, // 8 Zones
			18.60, // 9 Zones
			20.60 // 10+ Zones
	};

	public OndeVertePricing() {
		super(SINGLE_TICKET_VALIDTY, SINGLE_TICKET_PRICES, SINGLE_TICKET_PRICES_HALFFARE, DAY_TICKET_PRICES,
				DAY_TICKET_PRICES_HALFFARE);
	}
}
