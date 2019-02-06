package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.matsim.core.utils.collections.Tuple;

public class TriangleRegistry {
	private Collection<Triangle> triangles;
	private Map<Tuple<Long, Long>, Collection<Long>> connections = new HashMap<>();

	public TriangleRegistry(Collection<Triangle> triangles, Collection<Long> interchangeIds) {
		this.triangles = triangles;

		for (Triangle originTriangle : triangles) {
			for (Triangle destinationTriangle : triangles) {
				Tuple<Long, Long> connection = new Tuple<>(originTriangle.getTriangleId(),
						destinationTriangle.getTriangleId());
				connections.put(connection, new HashSet<>());

				Set<Long> originHafasIds = new HashSet<>(originTriangle.getHafasIds());
				Set<Long> destinationHafasIds = new HashSet<>(destinationTriangle.getHafasIds());
				originHafasIds.retainAll(destinationHafasIds);

				for (long connectionId : originHafasIds) {
					if (interchangeIds.contains(connectionId)) {
						connections.get(connection).add(connectionId);
					}
				}
			}
		}
	}

	public Collection<Triangle> getDirectTriangles(long originHafasId, long destinationHafasId) {
		Collection<Triangle> result = new HashSet<>();

		for (Triangle triangle : triangles) {
			if (triangle.contains(originHafasId) && triangle.contains(destinationHafasId)) {
				result.add(triangle);
			}
		}

		return result;
	}

	public Collection<Long> getConnectingIds(Triangle originTriangle, Triangle destinationTriangle) {
		Tuple<Long, Long> key = new Tuple<>(originTriangle.getTriangleId(), destinationTriangle.getTriangleId());
		return connections.getOrDefault(key, Collections.emptySet());
	}

	public Collection<Triangle> getTriangles(long hafasId) {
		Set<Triangle> result = new HashSet<>();

		for (Triangle triangle : triangles) {
			if (triangle.contains(hafasId)) {
				result.add(triangle);
			}
		}

		return result;
	}
}
