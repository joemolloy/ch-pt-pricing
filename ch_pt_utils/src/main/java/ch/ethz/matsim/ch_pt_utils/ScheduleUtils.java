package ch.ethz.matsim.ch_pt_utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.matsim.api.core.v01.Id;
import org.matsim.pt.transitSchedule.api.Departure;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitSchedule;

public class ScheduleUtils {
	public static void wrapSchedule(TransitSchedule schedule, double latestDeparture) {
		for (TransitLine transitLine : schedule.getTransitLines().values()) {
			for (TransitRoute transitRoute : transitLine.getRoutes().values()) {
				Collection<Departure> newDepartures = new LinkedList<>();

				for (Departure departure : transitRoute.getDepartures().values()) {
					double newDepartureTime = departure.getDepartureTime();
					int day = 1;

					while (newDepartureTime <= latestDeparture) {
						if (day > 1) {
							Id<Departure> newDepartureId = Id.create(departure.getId().toString() + "_day" + day,
									Departure.class);
							Departure newDeparture = schedule.getFactory().createDeparture(newDepartureId,
									newDepartureTime);
							newDepartures.add(newDeparture);
						}

						newDepartureTime += 24.0 * 3600.0;
						day++;
					}
				}

				newDepartures.forEach(transitRoute::addDeparture);
			}
		}
	}

	public static Collection<String> getVehicleModes(TransitSchedule schedule) {
		Set<String> modes = new HashSet<>();

		for (TransitLine transitLine : schedule.getTransitLines().values()) {
			for (TransitRoute transitRoute : transitLine.getRoutes().values()) {
				modes.add(transitRoute.getTransportMode());
			}
		}

		return modes;
	}
}
