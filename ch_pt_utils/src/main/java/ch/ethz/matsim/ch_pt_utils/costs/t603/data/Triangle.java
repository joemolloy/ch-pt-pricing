package ch.ethz.matsim.ch_pt_utils.costs.t603.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Triangle {
	private final String id;

	private final Collection<Station> stations = new HashSet<>();
	private final Map<Edge<Station>, Double> directDistances = new HashMap<>();
	private final Collection<Triangle> connectedTriangles = new HashSet<>();

	public Triangle(String id) {
		this.id = id;
	}

	public void addStation(Station station) {
		this.stations.add(station);
	}

	public Collection<Station> getStations() {
		return Collections.unmodifiableCollection(stations);
	}

	public void addDirectDistance(Station originStation, Station destinationStation, double distance) {
		directDistances.put(Edge.of(originStation, destinationStation), distance);
	}

	public boolean containsStation(Station station) {
		return stations.contains(station);
	}

	public void addConnectedTriangle(Triangle triangle) {
		if (triangle == this) {
			throw new IllegalStateException("Triangles should not be connected with themselves.");
		}

		this.connectedTriangles.add(triangle);
	}

	public Collection<Triangle> getConnectedTriangles() {
		return connectedTriangles;
	}

	public double getDirectDistance(Edge<Station> edge) {
		if (directDistances.containsKey(edge)) {
			return directDistances.get(edge);
		} else {
			throw new IllegalStateException(String.format("Direct distance %s does not exist for %s", edge, this));
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Triangle) {
			Triangle otherTriangle = (Triangle) other;
			return id.equals(otherTriangle.id);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return "Triangle(" + id + ")";
	}
}
