package ch.ethz.matsim.ch_pt_utils.costs.t603;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ethz.matsim.ch_pt_utils.costs.t603.data.Edge;
import ch.ethz.matsim.ch_pt_utils.costs.t603.data.Station;
import ch.ethz.matsim.ch_pt_utils.costs.t603.data.StationEdge;
import ch.ethz.matsim.ch_pt_utils.costs.t603.data.Triangle;
import ch.ethz.matsim.ch_pt_utils.costs.t603.raw.RawDistance;
import ch.ethz.matsim.ch_pt_utils.costs.t603.raw.RawStation;
import ch.ethz.matsim.ch_pt_utils.costs.t603.raw.RawTriangle;

public class T603 {
	private final Map<String, Station> stationsByName;
	private final Map<String, Triangle> triangles;
	private final Map<String, Station> stations;
	private final double[] priceTable;

	public T603(Map<String, Triangle> triangles, Map<String, Station> stations) {
		this.triangles = triangles;
		this.stations = stations;

		this.stationsByName = new HashMap<>();

		for (Station station : stations.values()) {
			if (stationsByName.containsKey(station.getName())) {
				throw new IllegalStateException(String.format("Multiple IDs for station %s", station.getName()));
			}

			stationsByName.put(station.getName(), station);
		}

		priceTable = createPriceTable();
	}

	private double[] createPriceTable() {
		double[] priceTable = new double[1500];

		for (int distanceIndex = 0; distanceIndex < 1500; distanceIndex++) {
			// First, calculation is based on distance classes (e.g. for distances <30km,
			// the distance is accounted for every 2nd kilometer).

			int distance = distanceIndex;

			if (distance <= 8) {
				distance = (int) (Math.ceil((double) distance / 4.0) * 4.0);
			} else if (distance <= 30) {
				distance = (int) (Math.ceil((double) distance / 2.0) * 2.0);
			} else if (distance <= 100) {
				distance = (int) (Math.ceil((double) distance / 3.0) * 3.0);
			} else if (distance <= 150) {
				distance = (int) (Math.ceil((double) distance / 5.0) * 5.0);
			} else if (distance <= 300) {
				distance = (int) (Math.ceil((double) distance / 10.0) * 10.0);
			} else {
				distance = (int) (Math.ceil((double) distance / 20.0) * 20.0);
			}

			// Second, prices per kilometer are different for distance classes. Each
			// kilometer has therefore its own "value".

			double price = 0.0;

			for (int currentDistance = 1; currentDistance < distance + 1; currentDistance++) {
				if (currentDistance <= 4) {
					price += 44.51;
				} else if (currentDistance <= 14) {
					price += 42.30;
				} else if (currentDistance <= 48) {
					price += 37.24;
				} else if (currentDistance <= 150) {
					price += 26.46;
				} else if (currentDistance <= 200) {
					price += 25.71;
				} else if (currentDistance <= 250) {
					price += 22.85;
				} else if (currentDistance <= 300) {
					price += 20.63;
				} else if (currentDistance <= 480) {
					price += 20.09;
				} else {
					price += 19.85;
				}
			}

			// Prices for distances <70km are rounded to 20 Rp, prices above to 1 Fr

			if (distance <= 69) {
				price = Math.ceil(price / 20.0) * 20.0;
			} else {
				price = Math.ceil(price / 100.0) * 100.0;
			}

			// The minimum price is 3 Fr

			price = Math.max(price, 300.0);
			priceTable[distanceIndex] = price / 100.0;
		}

		return priceTable;
	}

	public Station getStationById(String stationId) {
		if (stations.containsKey(stationId)) {
			return stations.get(stationId);
		} else {
			throw new IllegalStateException(String.format("Station with ID %s does not exist.", stationId));
		}
	}

	public Station getStationByName(String stationName) {
		if (stationsByName.containsKey(stationName)) {
			return stationsByName.get(stationName);
		} else {
			throw new IllegalStateException(String.format("Station with name %s does not exist.", stationName));
		}
	}

	private double computeDirectDistance(Station originStation, Station destinationStation,
			Set<Triangle> commonTriangles) {
		double minimumDistance = Double.POSITIVE_INFINITY;

		for (Triangle triangle : commonTriangles) {
			double distance = triangle.getDirectDistance(Edge.of(originStation, destinationStation));

			if (distance < minimumDistance) {
				minimumDistance = distance;
			}
		}

		return minimumDistance;
	}

	private double computeRoutedDistance(Station originStation, Station destinationStation) {
		int minimumEdgeCount = Integer.MAX_VALUE;
		List<List<Edge<Triangle>>> minimumEdgeRoutes = new LinkedList<>();

		List<List<Edge<Triangle>>> pending = new LinkedList<>();

		for (Triangle originTriangle : originStation.getTriangles()) {
			for (Triangle downstreamTriangle : originTriangle.getConnectedTriangles()) {
				Edge<Triangle> edge = Edge.of(originTriangle, downstreamTriangle);
				pending.add(Collections.singletonList(edge));
			}
		}

		while (pending.size() > 0) {
			List<Edge<Triangle>> currentRoute = pending.remove(0);

			Edge<Triangle> currentEdge = currentRoute.get(currentRoute.size() - 1);
			Triangle currentTriangle = currentEdge.getRight();

			if (currentTriangle.containsStation(destinationStation)) {
				// We found a full route

				if (currentRoute.size() < minimumEdgeCount) {
					minimumEdgeRoutes.clear();
					minimumEdgeCount = currentRoute.size();
				}

				if (currentRoute.size() == minimumEdgeCount) {
					minimumEdgeRoutes.add(currentRoute);
				}
			} else if (currentRoute.size() < minimumEdgeCount) {
				// The route isn't too long yet, we can further expand it

				for (Triangle newTriangle : currentTriangle.getConnectedTriangles()) {
					Edge<Triangle> newEdge = Edge.of(currentTriangle, newTriangle);

					if (!currentRoute.contains(newEdge)) {
						List<Edge<Triangle>> newRoute = new LinkedList<>(currentRoute);
						newRoute.add(newEdge);
						pending.add(newRoute);
					}
				}
			}
		}

		// Now we have a number of routes with minimum number of triangles.
		// We can now use them to calculate the shortest distance.
		return computeRoutedDistance(originStation, destinationStation, minimumEdgeRoutes);
	}

	private class StationRoute {
		public final List<Station> stations;
		public final double distance;

		public StationRoute() {
			stations = Collections.emptyList();
			distance = Double.POSITIVE_INFINITY;
		}

		public StationRoute(Station initialStation) {
			this.stations = Collections.singletonList(initialStation);
			this.distance = 0.0;
		}

		private StationRoute(List<Station> stations, double distance) {
			this.stations = stations;
			this.distance = distance;
		}

		public StationRoute extend(Station station, double distance) {
			List<Station> newStations = new LinkedList<>(stations);
			newStations.add(station);
			return new StationRoute(newStations, this.distance + distance);
		}

		public int size() {
			return stations.size();
		}

		public Station getLastStation() {
			return stations.get(stations.size() - 1);
		}
	}

	public Collection<StationEdge> getStationEdges(Triangle source, Triangle target) {
		Set<Station> commonStations = new HashSet<>();
		commonStations.addAll(source.getStations());
		commonStations.retainAll(target.getStations());
		return commonStations.stream().map(station -> new StationEdge(source, target, station))
				.collect(Collectors.toSet());
	}

	/**
	 * Here we already have a list of routes through triangles. However, triangles
	 * are connected at multiple points, so here we find the shortest distance path
	 * through these triangles.
	 */
	private double computeRoutedDistance(Station originStation, Station destinationStation,
			List<List<Edge<Triangle>>> triangleRoutes) {
		StationRoute minimumDistanceRoute = new StationRoute();

		for (List<Edge<Triangle>> triangleRoute : triangleRoutes) {
			List<StationRoute> pending = new LinkedList<>();
			pending.add(new StationRoute(originStation));

			while (pending.size() > 0) {
				StationRoute currentRoute = pending.remove(0);

				Edge<Triangle> triangleEdge = triangleRoute.get(currentRoute.size() - 1);
				Collection<StationEdge> connections = getStationEdges(triangleEdge.getLeft(), triangleEdge.getRight());
				boolean isLastConnection = currentRoute.size() == triangleRoute.size();

				for (StationEdge connection : connections) {
					double connectingDistance = connection.getSourceTriangle().getDirectDistance(
							Edge.of(currentRoute.getLastStation(), connection.getConnectingStation()));
					StationRoute newRoute = currentRoute.extend(connection.getConnectingStation(), connectingDistance);

					if (isLastConnection) {
						double lastDistance = connection.getTargetTriangle()
								.getDirectDistance(Edge.of(newRoute.getLastStation(), destinationStation));
						StationRoute finalRoute = newRoute.extend(destinationStation, lastDistance);

						if (finalRoute.distance < minimumDistanceRoute.distance) {
							minimumDistanceRoute = finalRoute;
						}
					} else if (newRoute.distance < minimumDistanceRoute.distance) {
						pending.add(newRoute);
					}
				}
			}
		}

		return minimumDistanceRoute.distance;
	}

	public double computeDistance(Station originStation, Station destinationStation) {
		Set<Triangle> commonTriangles = new HashSet<>();
		commonTriangles.addAll(originStation.getTriangles());
		commonTriangles.retainAll(destinationStation.getTriangles());

		if (commonTriangles.size() > 0) {
			return computeDirectDistance(originStation, destinationStation, commonTriangles);
		} else {
			return computeRoutedDistance(originStation, destinationStation);
		}
	}

	public static T603 read(File path) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<RawTriangle> rawTriangles = mapper.readValue(path,
				mapper.getTypeFactory().constructCollectionType(List.class, RawTriangle.class));

		Map<String, Triangle> triangles = new HashMap<>();
		Map<String, Station> stations = new HashMap<>();

		for (RawTriangle rawTriangle : rawTriangles) {
			Triangle triangle = new Triangle(rawTriangle.id);

			for (RawStation rawStation : rawTriangle.stations) {
				if (!stations.containsKey(rawStation.id)) {
					stations.put(rawStation.id, new Station(rawStation.id, rawStation.name));
				}

				Station station = stations.get(rawStation.id);

				station.addTriangle(triangle);
				triangle.addStation(station);
			}

			for (RawDistance distance : rawTriangle.matrix) {
				Station originStation = stations.get(distance.originId);
				Station destinationStation = stations.get(distance.destinationId);
				triangle.addDirectDistance(originStation, destinationStation, distance.distance);
			}

			triangles.put(rawTriangle.id, triangle);
		}

		for (Triangle originTriangle : triangles.values()) {
			for (Triangle destinationTriangle : triangles.values()) {
				if (originTriangle != destinationTriangle) {
					Set<Station> commonStations = new HashSet<>();
					commonStations.addAll(originTriangle.getStations());
					commonStations.retainAll(destinationTriangle.getStations());

					if (commonStations.size() > 0) {
						originTriangle.addConnectedTriangle(destinationTriangle);
					}
				}
			}
		}

		return new T603(triangles, stations);
	}

	public double computePrice(double distance) {
		int intDistance = (int) distance;
		return priceTable[intDistance];
	}

	public Map<String, Triangle> getTriangles() {
		return Collections.unmodifiableMap(triangles);
	}

	public Map<String, Station> getStations() {
		return Collections.unmodifiableMap(stations);
	}
}
