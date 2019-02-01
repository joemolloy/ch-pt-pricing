package ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets;

public abstract class ZVVStylePricing {
	private final double[] singleTicketValidity;

	private final double[] singleTicketPriceFullFare;
	private final double[] singleTicketPriceHalfFare;

	private final double[] dayTicketPriceFullFare;
	private final double[] dayTicketPriceHalfFare;

	private final int maximumNumberOfZones;

	protected ZVVStylePricing(double[] singleTicketValidity, double[] singleTicketPriceFullFare,
			double[] singleTicketPriceHalfFare, double[] dayTicketPriceFullFare, double[] dayTicketPriceHalfFare) {
		this.singleTicketValidity = singleTicketValidity;
		this.singleTicketPriceFullFare = singleTicketPriceFullFare;
		this.singleTicketPriceHalfFare = singleTicketPriceHalfFare;
		this.dayTicketPriceFullFare = dayTicketPriceFullFare;
		this.dayTicketPriceHalfFare = dayTicketPriceHalfFare;
		this.maximumNumberOfZones = singleTicketValidity.length + 1;

		if (singleTicketPriceFullFare.length + 1 != maximumNumberOfZones) {
			throw new IllegalStateException();
		}

		if (singleTicketPriceHalfFare.length + 1 != maximumNumberOfZones) {
			throw new IllegalStateException();
		}

		if (dayTicketPriceFullFare.length + 1 != maximumNumberOfZones) {
			throw new IllegalStateException();
		}

		if (dayTicketPriceHalfFare.length + 1 != maximumNumberOfZones) {
			throw new IllegalStateException();
		}
	}

	private int normalizeNumberOfZones(int numberOfZones) {
		if (numberOfZones < 1) {
			throw new IllegalStateException();
		}

		return Math.min(maximumNumberOfZones, numberOfZones);
	}

	public double getSingleTicketPrice(int numberOfZones, boolean halfFare) {
		numberOfZones = normalizeNumberOfZones(numberOfZones);

		if (halfFare) {
			return singleTicketPriceHalfFare[numberOfZones];
		} else {
			return singleTicketPriceFullFare[numberOfZones];
		}
	}

	public double getSingleTicketValidity(int numberOfZones) {
		numberOfZones = normalizeNumberOfZones(numberOfZones);
		return singleTicketValidity[numberOfZones] * 60.0;
	}

	public double getDayTicketPrice(int numberOfZones, boolean halfFare) {
		numberOfZones = normalizeNumberOfZones(numberOfZones);

		if (halfFare) {
			return dayTicketPriceHalfFare[numberOfZones];
		} else {
			return dayTicketPriceFullFare[numberOfZones];
		}
	}
	
	@SuppressWarnings("unused")
	private final static double[] TEMPLATE = new double[] { //
			0.0, // 
			0.0, // 1 Zone
			0.0, // 2 Zones
			0.0, // 3 Zones
			0.0, // 4 Zones
			0.0, // 5 Zones
			0.0, // 6 Zones
			0.0, // 7 Zones 
			0.0 // 8+ Zones
	};

}
