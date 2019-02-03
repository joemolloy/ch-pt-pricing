package ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.pricing;

import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.ZVVStylePricing;

public class UniresoPricing extends ZVVStylePricing {
	private final static double[] SINGLE_TICKET_VALIDTY = new double[] { //
			0.0, //
			60.0, // 1 Zone
			60.0, // 2 Zones
			120.0, // 3 Zones
			120.0, // 4 Zones
			120.0, // 5 Zones
			120.0 // 6+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES = new double[] { //
			0.0, //
			3.40, // 1 Zone
			3.40, // 2 Zones
			4.80, // 3 Zones
			6.60, // 4 Zones
			8.40, // 5 Zones
			9.80 // 6+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			2.40, // 1 Zone
			2.40, // 2 Zones
			2.90, // 3 Zones
			3.30, // 4 Zones
			4.20, // 5 Zones
			4.90, // 6+ Zones
	};

	private final static double[] DAY_TICKET_PRICES = new double[] { //
			0.0, //
			18.50, // 1 Zone
			18.50,  // 2 Zones
			18.50, // 3 Zones
			18.50, // 4 Zones
			18.50, // 5 Zones
			18.50, // 6+ Zones
	};

	private final static double[] DAY_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			18.50, // 1 Zone
			18.50,  // 2 Zones
			18.50, // 3 Zones
			18.50, // 4 Zones
			18.50, // 5 Zones
			18.50, // 6+ Zones
	};

	public UniresoPricing() {
		super(SINGLE_TICKET_VALIDTY, SINGLE_TICKET_PRICES, SINGLE_TICKET_PRICES_HALFFARE, DAY_TICKET_PRICES,
				DAY_TICKET_PRICES_HALFFARE);
	}
}
