package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.pricing;

import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.StaticZoneTicketCalculator;

public class OstwindPricing extends StaticZoneTicketCalculator {
	private final static double[] SINGLE_TICKET_VALIDTY = new double[] { //
			0.0, //
			60.0, // 1 Zone
			60.0, // 2 Zones
			60.0, // 3 Zones
			120.0, // 4 Zones
			120.0, // 5 Zones
			120.0, // 6 Zones
			120.0, // 7 Zones
			120.0, // 8 Zones
			120.0, // 9 Zones
			120.0, // 10 Zones
			120.0, // 11 Zones
			120.0, // 12 Zones
			180.0 // 13+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES = new double[] { //
			0.0, //
			3.20, // 1 Zones
			4.80, // 2 Zones
			7.20, // 3 Zones
			9.60, // 4 Zones
			12.00, // 5 Zones
			14.40, // 6 Zones
			16.80, // 7 Zones
			19.20, // 8 Zones
			21.60, // 9 Zones
			24.00, // 10 Zones
			26.20, // 11 Zones
			28.40, // 12 Zones
			30.60 // 13+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			2.60, // 1 Zone
			2.90, // 2 Zones
			3.60, // 3 Zones
			4.80, // 4 Zones
			6.00, // 5 Zones
			7.20, // 6 Zones
			8.40, // 7 Zones
			9.60, // 8 Zones
			10.80, // 9 Zones
			12.00, // 10 Zones
			13.10, // 11 Zones
			14.20, // 12 Zones
			15.30 // 13+ Zones
	};

	private final static double[] DAY_TICKET_PRICES = new double[] { //
			0.0, //
			6.40, // 1 Zone
			9.60, // 2 Zones
			14.40, // 3 Zones
			19.20, // 4 Zones
			24.00, // 5 Zones
			28.80, // 6 Zones
			33.60, // 7 Zones
			38.40, // 8 Zones
			43.20, // 9 Zones
			48.00, // 10 Zones
			52.40, // 11 Zones
			56.80, // 12 Zones
			61.20 // 13+ Zones
	};

	private final static double[] DAY_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			5.20, // 1 Zone
			5.80, // 2 Zones
			7.20, // 3 Zones
			9.60, // 4 Zones
			12.00, // 5 Zones
			14.40, // 6 Zones
			16.80, // 7 Zones
			19.20, // 8 Zones
			21.60, // 9 Zones
			24.00, // 10 Zones
			26.20, // 11 Zones
			28.40, // 12 Zones
			30.60 // 13+ Zones
	};

	public OstwindPricing() {
		super(SINGLE_TICKET_VALIDTY, SINGLE_TICKET_PRICES, SINGLE_TICKET_PRICES_HALFFARE, DAY_TICKET_PRICES,
				DAY_TICKET_PRICES_HALFFARE);
	}
}
