package ch.ethz.matsim.ch_pt_utils.cost.sbb.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.matsim.core.utils.collections.Tuple;

public class Triangle {
	private final long triangleId;
	private final List<Long> hafasIds;
	private final double[][] distances;

	private Triangle(long triangleId, List<Long> hafasIds, double[][] distances) {
		this.triangleId = triangleId;
		this.hafasIds = hafasIds;
		this.distances = distances;
	}

	public long getTriangleId() {
		return triangleId;
	}

	public boolean contains(long hafasId) {
		return hafasIds.contains(hafasId);
	}

	public double getDistance(long originHafasId, long destinationHafasId) {
		int originIndex = hafasIds.indexOf(originHafasId);
		int destinationIndex = hafasIds.indexOf(destinationHafasId);
		return distances[originIndex][destinationIndex];
	}

	public Collection<Long> getHafasIds() {
		return hafasIds;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Triangle) {
			Triangle otherTriangle = (Triangle) other;
			return otherTriangle.triangleId == triangleId;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return (int) triangleId;
	}

	@Override
	public String toString() {
		return "T(" + triangleId + ")";
	}

	static public class Builder {
		private final long triangleId;
		private final Set<Long> hafasIds = new HashSet<>();
		private final Map<Tuple<Long, Long>, Double> distances = new HashMap<>();

		public Builder(long triangleId) {
			this.triangleId = triangleId;
		}

		public void addDistance(long originHafasId, long destinationHafasId, double distance) {
			hafasIds.add(originHafasId);
			hafasIds.add(destinationHafasId);
			distances.put(new Tuple<>(originHafasId, destinationHafasId), distance);
		}

		public Triangle build() {
			List<Long> hafasIdsList = new ArrayList<>(hafasIds);
			double[][] matrix = new double[hafasIds.size()][hafasIds.size()];

			for (int i = 0; i < hafasIds.size(); i++) {
				for (int j = 0; j < hafasIds.size(); j++) {
					Tuple<Long, Long> key = new Tuple<>(hafasIdsList.get(i), hafasIdsList.get(j));

					if (distances.containsKey(key)) {
						matrix[i][j] = distances.get(key);
					} else {
						matrix[i][j] = Double.POSITIVE_INFINITY;
					}
				}
			}

			return new Triangle(triangleId, hafasIdsList, matrix);
		}
	}
}
