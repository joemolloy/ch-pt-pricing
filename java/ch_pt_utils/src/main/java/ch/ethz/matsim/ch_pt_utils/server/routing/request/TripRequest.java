package ch.ethz.matsim.ch_pt_utils.server.routing.request;

import org.matsim.core.utils.misc.Time;

public class TripRequest {
	public double originLongitude;
	public double originLatitude;
	public double destinationLongitude;
	public double destinationLatitude;
	public double departureTime;

	@Override
	public String toString() {
		return String.format("Request([%f, %f] -> [%f, %f] @ %s)", originLongitude, originLatitude,
				destinationLongitude, destinationLatitude, Time.writeTime(departureTime));
	}
}
