package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.pricing;

import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.StaticZoneTicketCalculator;

public class AWellePricing extends StaticZoneTicketCalculator {
	private final static double[] SINGLE_TICKET_VALIDTY = new double[] { //
			0.0, //
			30.0, // 1 Zone
			60.0, // 2 Zones
			60.0, // 3 Zones
			120.0, // 4 Zones
			120.0, // 5 Zones
			120.0, // 6 Zones
			120.0, // 7 Zones
			180.0, // 8+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES = new double[] { //
			0.0, //
			3.60, // 1 Zone
			5.00, // 2 Zones
			7.20, // 3 Zones
			9.40, // 4 Zones
			11.80, // 5 Zones
			14.00, // 6 Zones
			16.40, // 7 Zones
			18.80, // 8+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			2.80, // 1 Zone
			3.40, // 2 Zones
			4.00, // 3 Zones
			4.70, // 4 Zones
			5.90, // 5 Zones
			7.00, // 6 Zones
			8.20, // 7 Zones
			9.40, // 8+ Zones
	};

	private final static double[] DAY_TICKET_PRICES = new double[] { //
			0.0, //
			10.0, // 1 Zone
			10.0, // 2 Zones
			14.40, // 3 Zones
			18.80, // 4 Zones
			23.60, // 5 Zones
			28.00, // 6 Zones
			32.80, // 7 Zones
			37.60, // 8+ Zones
	};

	private final static double[] DAY_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			6.80, // 1 Zone
			6.80, // 2 Zones
			8.00, // 3 Zones
			9.40, // 4 Zones
			11.80, // 5 Zones
			14.00, // 6 Zones
			16.40, // 7 Zones
			18.80, // 8+ Zones
	};

	public AWellePricing() {
		super(SINGLE_TICKET_VALIDTY, SINGLE_TICKET_PRICES, SINGLE_TICKET_PRICES_HALFFARE, DAY_TICKET_PRICES,
				DAY_TICKET_PRICES_HALFFARE);
	}
}
