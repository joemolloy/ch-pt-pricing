package ch.ethz.matsim.ch_pt_utils.costs.t603.data;

public class StationEdge {
	private final Triangle sourceTriangle;
	private final Triangle targetTriangle;
	private final Station connectingStation;

	public StationEdge(Triangle sourceTriangle, Triangle targetTriangle, Station connectingStation) {
		this.sourceTriangle = sourceTriangle;
		this.targetTriangle = targetTriangle;
		this.connectingStation = connectingStation;
	}

	public Triangle getSourceTriangle() {
		return sourceTriangle;
	}

	public Triangle getTargetTriangle() {
		return targetTriangle;
	}

	public Station getConnectingStation() {
		return connectingStation;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof StationEdge) {
			StationEdge otherEdge = (StationEdge) other;
			return sourceTriangle.equals(otherEdge.sourceTriangle) && targetTriangle.equals(otherEdge.targetTriangle)
					&& connectingStation.equals(otherEdge.connectingStation);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return sourceTriangle.hashCode() + targetTriangle.hashCode() + connectingStation.hashCode();
	}

	@Override
	public String toString() {
		return String.format("StationEdge(%s -> %s over %s)", sourceTriangle, targetTriangle, connectingStation);
	}
}
