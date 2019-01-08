package ch.ethz.matsim.ch_pt_utils.costs.t603.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class Station {
	private final String id;
	private final String name;
	private final Collection<Triangle> triangles = new HashSet<>();

	public Station(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addTriangle(Triangle triangle) {
		this.triangles.add(triangle);
	}

	public Collection<Triangle> getTriangles() {
		return Collections.unmodifiableCollection(triangles);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Station) {
			Station otherStation = (Station) other;
			return id.equals(otherStation.id);
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
		return "Station(" + name + ", " + id + ")";
	}
}
