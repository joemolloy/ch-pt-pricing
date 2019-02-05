package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.pricing;

import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.StaticZoneTicketCalculator;

public class EngadinMobilPricing extends StaticZoneTicketCalculator {
	private final static double[] SINGLE_TICKET_VALIDTY = new double[] { //
			0.0, //
			60.0, // 1 Zone
			60.0, // 2 Zones
			120.0, // 3 Zones
			120.0, // 4 Zones
			120.0, // 5 Zones
			120.0, // 6 Zones
			180.0, // 7+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES = new double[] { //
			0.0, //
			3.00, // 1 Zone
			5.60, // 2 Zones
			8.40, // 3 Zones
			11.20, // 4 Zones
			14.00, // 5 Zones
			16.80, // 6 Zones
			19.60, // 7+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			2.20, // 1 Zone
			2.80, // 2 Zones
			4.20, // 3 Zones
			5.60, // 4 Zones
			7.00, // 5 Zones
			8.40, // 6 Zones
			9.80, // 7+ Zones
	};

	private final static double[] DAY_TICKET_PRICES = new double[] { //
			0.0, //
			6.0, // 1 Zone
			11.20, // 2 Zones
			16.80, // 3 Zones
			22.40, // 4 Zones
			28.00, // 5 Zones
			33.60, // 6 Zones
			33.60, // 7+ Zones
	};

	private final static double[] DAY_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			4.40, // 1 Zone
			5.60, // 2 Zones
			8.40, // 3 Zones
			11.20, // 4 Zones
			14.00, // 5 Zones
			16.80, // 6 Zones
			16.80, // 7+ Zones
	};

	public EngadinMobilPricing() {
		super(SINGLE_TICKET_VALIDTY, SINGLE_TICKET_PRICES, SINGLE_TICKET_PRICES_HALFFARE, DAY_TICKET_PRICES,
				DAY_TICKET_PRICES_HALFFARE);
	}
}
