package ch.ethz.matsim.ch_pt_utils.cost.zonal.data;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ZonalStage {
	private final List<ZonalWaypoint> waypoints = new LinkedList<>();
	private final double departureTime;
	private final double arrivalTime;

	public ZonalStage(double departureTime, double arrivalTime) {
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
	}

	public void addWaypoint(ZonalWaypoint waypoint) {
		waypoints.add(waypoint);
	}

	public double getDepartureTime() {
		return departureTime;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public List<ZonalWaypoint> getWaypoints() {
		return waypoints;
	}

	public boolean isCovered(double maximumTime) {
		return arrivalTime <= maximumTime;
	}

	public boolean isCovered(Collection<Zone> zones) {
		for (ZonalWaypoint waypoint : waypoints) {
			if (!waypoint.isCovered(zones)) {
				return false;
			}
		}
		
		return true;
	}
}
