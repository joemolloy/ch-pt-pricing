package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.pricing;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.StaticZoneTicketCalculator;

public class TVZGPricing extends StaticZoneTicketCalculator {
	private final static double[] SINGLE_TICKET_VALIDTY = new double[] { //
			0.0, //
			30.0, // 1 Zone
			60.0, // 2 Zones
			60.0, // 3 Zones
			120.0, // 4 Zones
			120.0, // 5+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES = new double[] { //
			0.0, //
			3.30, // 1 Zone
			4.30, // 2 Zones
			5.80, // 3 Zones
			7.80, // 4 Zones
			8.40, // 5+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			2.70, // 1 Zone
			3.10, // 2 Zones
			3.50, // 3 Zones
			3.90, // 4 Zones
			4.20, // 5+ Zones
	};

	private final static double[] DAY_TICKET_PRICES = new double[] { //
			0.0, //
			6.60, // 1 Zone
			8.60, // 2 Zones
			11.60, // 3 Zones
			15.60, // 4 Zones
			16.80, // 5+ Zones
	};

	private final static double[] DAY_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			5.40, // 1 Zone
			6.20, // 2 Zones
			7.00, // 3 Zones
			7.80, // 4 Zones
			8.40, // 5+ Zones
	};

	public TVZGPricing() {
		super(SINGLE_TICKET_VALIDTY, SINGLE_TICKET_PRICES, SINGLE_TICKET_PRICES_HALFFARE, DAY_TICKET_PRICES,
				DAY_TICKET_PRICES_HALFFARE);
	}
}
