package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.pricing;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.StaticZoneTicketCalculator;

public class LiberoPricing extends StaticZoneTicketCalculator {
	private final static double[] SINGLE_TICKET_VALIDTY = new double[] { //
			0.0, //
			60.0, // 1 Zone
			60.0, // 2 Zones
			90.0, // 3 Zones
			90.0, // 4 Zones
			120.0, // 5 Zones
			120.0, // 6 Zones
			120.0, // 7 Zones
			180.0, // 8 Zones
			180.0, // 9 Zones
			180.0, // 10 Zones
			180.0, // 11 Zones
			180.0 // 12+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES = new double[] { //
			0.0, //
			4.60, // 1 Zone
			4.60, // 2 Zones
			7.00, // 3 Zones
			9.20, // 4 Zones
			11.60, // 5 Zones
			14.00, // 6 Zones
			16.20, // 7 Zones
			18.40, // 8 Zones
			20.80, // 9 Zones
			23.20, // 10 Zones
			25.60, // 11 Zones
			28.00 // 12+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			2.80, // 1 Zone
			2.80, // 2 Zones
			3.70, // 3 Zones
			4.60, // 4 Zones
			5.80, // 5 Zones
			7.00, // 6 Zones
			8.10, // 7 Zones
			9.20, // 8 Zones
			10.40, // 9 Zones
			11.60, // 10 Zones
			12.80, // 11 Zones
			14.00 // 12+ Zones
	};

	private final static double[] DAY_TICKET_PRICES = new double[] { //
			0.0, //
			13.00, // 1 Zone
			13.00, // 2 Zones
			17.60, // 3 Zones
			21.20, // 4 Zones
			25.60, // 5 Zones
			28.00, // 6 Zones
			32.40, // 7 Zones
			36.80, // 8 Zones
			41.60, // 9 Zones
			46.40, // 10 Zones
			51.20, // 11 Zones
			56.00 // 12+ Zones
	};

	private final static double[] DAY_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			7.90, // 1 Zone
			7.90, // 2 Zone
			9.30, // 3 Zones
			10.60, // 4 Zones
			12.80, // 5 Zones
			14.00, // 6 Zones
			16.20, // 7 Zones
			18.40, // 8 Zones
			20.80, // 9 Zones
			23.20, // 10 Zones
			25.60, // 11 Zones
			28.00, // 12+ Zones
	};

	public LiberoPricing() {
		super(SINGLE_TICKET_VALIDTY, SINGLE_TICKET_PRICES, SINGLE_TICKET_PRICES_HALFFARE, DAY_TICKET_PRICES,
				DAY_TICKET_PRICES_HALFFARE);
	}
}
