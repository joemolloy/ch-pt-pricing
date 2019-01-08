package ch.ethz.matsim.ch_pt_utils.costs.zonal.data;

import org.matsim.api.core.v01.Coord;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class Zone {
	private static final GeometryFactory geometryFactory = new GeometryFactory();

	private final String name;
	private final ZoneType type;
	private final ZoneGroup group;

	private Geometry geometry;

	public Zone(String name, ZoneType type, ZoneGroup group) {
		this.name = name;
		this.type = type;
		this.group = group;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	public boolean contains(Coord coord) {
		Point point = geometryFactory.createPoint(new Coordinate(coord.getX(), coord.getY()));
		return geometry.contains(point);
	}

	public String getName() {
		return name;
	}

	public ZoneType getType() {
		return type;
	}

	public ZoneGroup getGroup() {
		return group;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Zone) {
			Zone otherZone = (Zone) other;
			return otherZone.name.equals(name);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return String.format("Zone(%s)", name);
	}
}
