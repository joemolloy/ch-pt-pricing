package ch.ethz.matsim.ch_pt_utils.costs.t603.raw;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class RawDistance {
	public String originId;
	public String destinationId;
	public double distance;
	
	@JsonCreator
	public RawDistance(List<Object> data) {
		originId = (String) data.get(0);
		destinationId = (String) data.get(1);
		
		Object distance = data.get(2);
		
		if (distance instanceof Double) {
			this.distance = (Double) distance;
		} else {
			this.distance = (Integer) distance;
		}
	}
}
