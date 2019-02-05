package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.pricing;

import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.StaticZoneTicketCalculator;

public class FrimobilPricing extends StaticZoneTicketCalculator {
	private final static double[] SINGLE_TICKET_VALIDTY = new double[] { //
			0.0, //
			60.0, // 1 Zone
			120.0, // 2 Zones
			120.0, // 3 Zones
			120.0, // 4 Zones
			120.0, // 5 Zones
			120.0, // 6 Zones
			180.0, // 7+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES = new double[] { //
			0.0, //
			2.90, // 1 Zone
			5.20, // 2 Zones
			7.60, // 3 Zones
			10.0, // 4 Zones
			12.40, // 5 Zones
			14.60, // 6 Zones
			16.80, // 7+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			2.20, // 1 Zone
			3.10, // 2 Zones
			3.80, // 3 Zones
			5.00, // 4 Zones
			6.20, // 5 Zones
			7.30, // 6 Zones
			8.40, // 7+ Zones
	};

	private final static double[] DAY_TICKET_PRICES = new double[] { //
			0.0, //
			8.80, // 1 Zone
			11.20, // 2 Zones
			15.20, // 3 Zones
			20.0, // 4 Zones
			24.80, // 5 Zones
			29.20, // 6 Zones
			33.60, // 7+ Zones
	};

	private final static double[] DAY_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			5.70, // 1 Zone
			6.30, // 2 Zones
			7.60, // 3 Zones
			10.0, // 4 Zones
			12.40, // 5 Zones
			14.60, // 6 Zones
			16.80, // 7+ Zones
	};

	public FrimobilPricing() {
		super(SINGLE_TICKET_VALIDTY, SINGLE_TICKET_PRICES, SINGLE_TICKET_PRICES_HALFFARE, DAY_TICKET_PRICES,
				DAY_TICKET_PRICES_HALFFARE);
	}
}
