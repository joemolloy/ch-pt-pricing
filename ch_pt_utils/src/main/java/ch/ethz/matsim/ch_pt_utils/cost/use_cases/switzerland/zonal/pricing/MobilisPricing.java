package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.pricing;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.StaticZoneTicketCalculator;

public class MobilisPricing extends StaticZoneTicketCalculator {
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
			180.0, // 10 Zones
			240.0, // 11 Zones
			240.0, // 12 Zones
			240.0, // 13 Zones
			240.0, // 14 Zones
			240.0 // 15+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES = new double[] { //
			0.0, //
			3.00, // 1 Zones
			3.70, // 2 Zones
			5.60, // 3 Zones
			7.40, // 4 Zones
			9.20, // 5 Zones
			11.20, // 6 Zones
			13.00, // 7 Zones
			14.80, // 8 Zones
			16.60, // 9 Zones
			18.60, // 10 Zones
			20.40, // 11 Zones
			22.20, // 12 Zones
			24.00, // 13 Zones
			25.80, // 14 Zones
			27.80 // 15+ Zones
	};

	private final static double[] SINGLE_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			2.40, // 1 Zone
			2.40, // 2 Zones
			2.80, // 3 Zones
			3.70, // 4 Zones
			4.60, // 5 Zones
			5.60, // 6 Zones
			6.50, // 7 Zones
			7.40, // 8 Zones
			8.30, // 9 Zones
			9.30, // 10 Zones
			10.20, // 11 Zones
			11.10, // 12 Zones
			12.00, // 13 Zones
			12.90, // 14 Zones
			13.90 // 15+ Zones
	};

	private final static double[] DAY_TICKET_PRICES = new double[] { //
			0.0, //
			8.60, // 1 Zone
			9.30, // 2 Zones
			11.20, // 3 Zones
			14.80, // 4 Zones
			18.40, // 5 Zones
			22.40, // 6 Zones
			26.00, // 7 Zones
			29.60, // 8 Zones
			33.20, // 9 Zones
			37.20, // 10 Zones
			40.80, // 11 Zones
			44.40, // 12 Zones
			48.00, // 13 Zones
			51.60, // 14 Zones
			55.60 // 15+ Zones
	};

	private final static double[] DAY_TICKET_PRICES_HALFFARE = new double[] { //
			0.0, //
			6.90, // 1 Zone
			6.90, // 2 Zones
			7.00, // 3 Zones
			7.40, // 4 Zones
			9.20, // 5 Zones
			11.20, // 6 Zones
			13.00, // 7 Zones
			14.80, // 8 Zones
			16.60, // 9 Zones
			18.60, // 10 Zones
			20.40, // 11 Zones
			22.20, // 12 Zones
			24.00, // 13 Zones
			25.80, // 14 Zones
			27.80 // 15+ Zones
	};

	public MobilisPricing() {
		super(SINGLE_TICKET_VALIDTY, SINGLE_TICKET_PRICES, SINGLE_TICKET_PRICES_HALFFARE, DAY_TICKET_PRICES,
				DAY_TICKET_PRICES_HALFFARE);
	}
}
