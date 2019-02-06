package ch.ethz.matsim.ch_pt_utils.cost.stages;

import java.util.List;

public class TransitStage {
	private final List<Long> hafasIds;
	private final double departureTime;
	private final double arrivalTime;
	private final double distance;
	private final String mode;

	public TransitStage(List<Long> hafasIds, double distance, double departureTime, double arrivalTime, String mode) {
		this.hafasIds = hafasIds;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
		this.distance = distance;
		this.mode = mode;
	}

	public List<Long> getHafasIds() {
		return hafasIds;
	}

	public double getDepartureTime() {
		return departureTime;
	}

	public double getArrivalTime() {
		return arrivalTime;
	}

	public double getDistance() {
		return distance;
	}

	public String getMode() {
		return mode;
	}
}
