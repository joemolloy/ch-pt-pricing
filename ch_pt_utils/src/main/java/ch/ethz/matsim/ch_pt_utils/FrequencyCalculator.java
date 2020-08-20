package ch.ethz.matsim.ch_pt_utils;

import java.lang.reflect.Field;
import java.util.List;

import org.matsim.facilities.Facility;

import ch.sbb.matsim.routing.pt.raptor.RaptorRoute;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptor;

public class FrequencyCalculator {
	private final SwissRailRaptor raptor;

	private final double beforeDepartureOffset;
	private final double afterDepartureOffset;

	public FrequencyCalculator(SwissRailRaptor raptor, double beforeDepartureOffset, double afterDepartureOffset) {
		this.raptor = raptor;
		this.beforeDepartureOffset = beforeDepartureOffset;
		this.afterDepartureOffset = afterDepartureOffset;
	}

	public double calculateFrequency(Facility<?> originFacility, Facility<?> destinationFacility, double departureTime) {
		double earliestDepartureTime = departureTime - beforeDepartureOffset;
		double latestDepartureTime = departureTime + afterDepartureOffset;
		return calculateFrequency(originFacility, destinationFacility, departureTime, earliestDepartureTime, latestDepartureTime);
	}

	public double calculateFrequency(Facility<?> originFacility, Facility<?> destinationFacility, double departureTime, double frequencyWindowStart, double frequencyWindowEnd) {
		List<RaptorRoute> routes = raptor.calcRoutes(originFacility, destinationFacility, frequencyWindowStart,
				departureTime, frequencyWindowEnd, null);

		int numberOfPtRoutes = 0;

		for (RaptorRoute route : routes) {
			if (getPtLegCount(route) > 0) {
				numberOfPtRoutes++;
			}
		}

		//number of routes per hour
		return numberOfPtRoutes / ((beforeDepartureOffset + afterDepartureOffset) / 3600);
	}

	// Reflection magic starting here :)

	private final static Field PT_LEG_COUNT_FIELD;

	static {
		try {
			PT_LEG_COUNT_FIELD = RaptorRoute.class.getDeclaredField("ptLegCount");
			PT_LEG_COUNT_FIELD.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	private int getPtLegCount(RaptorRoute route) {
		try {
			return PT_LEG_COUNT_FIELD.getInt(route);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
