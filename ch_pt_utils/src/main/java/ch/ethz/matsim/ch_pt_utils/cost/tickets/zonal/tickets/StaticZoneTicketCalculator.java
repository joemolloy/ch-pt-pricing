package ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets;

import java.util.Set;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.ZonalTicketCalculator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Zone;

public abstract class StaticZoneTicketCalculator implements ZonalTicketCalculator {
	private final double[] singleTicketValidity;

	private final double[] singleTicketPriceFullFare;
	private final double[] singleTicketPriceHalfFare;

	private final double[] dayTicketPriceFullFare;
	private final double[] dayTicketPriceHalfFare;

	private final int maximumNumberOfZones;

	protected StaticZoneTicketCalculator(double[] singleTicketValidity, double[] singleTicketPriceFullFare,
			double[] singleTicketPriceHalfFare, double[] dayTicketPriceFullFare, double[] dayTicketPriceHalfFare) {
		this.singleTicketValidity = singleTicketValidity;
		this.singleTicketPriceFullFare = singleTicketPriceFullFare;
		this.singleTicketPriceHalfFare = singleTicketPriceHalfFare;
		this.dayTicketPriceFullFare = dayTicketPriceFullFare;
		this.dayTicketPriceHalfFare = dayTicketPriceHalfFare;
		this.maximumNumberOfZones = singleTicketValidity.length;

		if (singleTicketPriceFullFare.length != maximumNumberOfZones) {
			throw new IllegalStateException();
		}

		if (singleTicketPriceHalfFare.length != maximumNumberOfZones) {
			throw new IllegalStateException();
		}

		if (dayTicketPriceFullFare.length != maximumNumberOfZones) {
			throw new IllegalStateException();
		}

		if (dayTicketPriceHalfFare.length != maximumNumberOfZones) {
			throw new IllegalStateException();
		}
	}

	private int normalizeNumberOfZones(int numberOfZones) {
		if (numberOfZones < 1) {
			throw new IllegalStateException();
		}

		return Math.min(maximumNumberOfZones - 1, numberOfZones);
	}

	protected int calculateNumberOfZones(Set<Zone> zones) {
		int numberOfZones = zones.size();

		for (Zone zone : zones) {
			if (zone.getZoneId() == 110 || zone.getZoneId() == 120) {
				numberOfZones++;
			}
		}

		return numberOfZones;
	}

	@Override
	public double calculateValidity(Set<Zone> zones) {
		int numberOfZones = normalizeNumberOfZones(calculateNumberOfZones(zones));
		return singleTicketValidity[numberOfZones] * 60.0;
	}

	@Override
	public double calculateSingleTicketPrice(Set<Zone> zones, boolean halfFare) {
		int numberOfZones = normalizeNumberOfZones(calculateNumberOfZones(zones));

		if (halfFare) {
			return singleTicketPriceHalfFare[numberOfZones];
		} else {
			return singleTicketPriceFullFare[numberOfZones];
		}
	}

	@Override
	public double calculateDayTicketPrice(Set<Zone> zones, boolean halfFare) {
		int numberOfZones = normalizeNumberOfZones(calculateNumberOfZones(zones));

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
