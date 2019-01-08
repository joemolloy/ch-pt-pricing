package ch.ethz.matsim.ch_pt_utils.costs.t603.data;

import org.matsim.api.core.v01.Coord;

public class SpatialStation {
	public final Station station;
	public final Coord coord;

	public SpatialStation(Station station, Coord coord) {
		this.station = station;
		this.coord = coord;
	}
}
