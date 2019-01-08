package ch.ethz.matsim.ch_pt_utils.costs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.geometry.CoordUtils;

import ch.ethz.matsim.ch_pt_utils.costs.t603.SpatialT603;
import ch.ethz.matsim.ch_pt_utils.costs.t603.T603;
import ch.ethz.matsim.ch_pt_utils.costs.t603.data.SpatialStation;
import ch.ethz.matsim.ch_pt_utils.costs.t603.data.Station;
import ch.ethz.matsim.ch_pt_utils.costs.zonal.ZonalSystem;
import ch.ethz.matsim.ch_pt_utils.costs.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.costs.zonal.data.ZoneGroup;

public class CostCalculator {
	private final T603 t603;
	private final SpatialT603 spatialT603;
	private final ZonalSystem zonalSystem;

	public CostCalculator(T603 t603, SpatialT603 spatialT603, ZonalSystem zonalSystem) {
		this.t603 = t603;
		this.spatialT603 = spatialT603;
		this.zonalSystem = zonalSystem;
	}

	private List<List<Integer>> findConsecutiveLongDistanceIndices(int numberOfStops, Set<Integer> candidates) {
		// First, remove all indices which are not part of connected long distance
		// chains

		Set<Integer> selectedIndices = new HashSet<>(candidates);
		Set<Integer> removal = new HashSet<>();
		removal.add(0);

		while (removal.size() > 0) {
			removal.clear();

			for (int index : selectedIndices) {
				if (!selectedIndices.contains(index - 1) && !selectedIndices.contains(index + 1)) {
					removal.add(index);
				}
			}

			removal.forEach(index -> selectedIndices.remove(index));
		}

		// longDistanceIndices now contains all indices that are part of a long distance
		// chain. We now need to extract the chains.

		List<Integer> currentChain = new LinkedList<>();
		List<List<Integer>> chains = new LinkedList<>();

		for (int i = 0; i < numberOfStops; i++) {
			if (selectedIndices.contains(i)) {
				currentChain.add(i);
			} else {
				chains.add(currentChain);
				currentChain = new LinkedList<>();
			}
		}

		if (currentChain.size() > 0) {
			chains.add(currentChain);
		}

		return chains;
	}

	private List<List<Integer>> findConsecutiveZonalIndices(int numberOfStops, Set<Integer> candidates,
			List<Optional<Zone>> zones) {
		// First, remove all indices which are not part of connected zonal chains

		Set<Integer> selectedIndices = new HashSet<>(candidates);
		Set<Integer> removal = new HashSet<>();
		removal.add(0);

		while (removal.size() > 0) {
			removal.clear();

			for (int index : selectedIndices) {
				if (!selectedIndices.contains(index - 1) && !selectedIndices.contains(index + 1)) {
					removal.add(index);
				}
			}

			removal.forEach(index -> selectedIndices.remove(index));
		}

		// longDistanceIndices now contains all indices that are part of a zonal chain.
		// We now need to extract the chains.

		List<Integer> currentChain = new LinkedList<>();
		List<List<Integer>> chains = new LinkedList<>();
		ZoneGroup previousGroup = ZoneGroup.UNKNOWN;

		for (int i = 0; i < numberOfStops; i++) {
			if (selectedIndices.contains(i)) {
				ZoneGroup currentGroup = zones.get(i).get().getGroup();

				if (!previousGroup.equals(currentGroup)) {
					chains.add(currentChain);
					currentChain = new LinkedList<>();
					previousGroup = currentGroup;
				}

				currentChain.add(i);
			} else {
				chains.add(currentChain);
				currentChain = new LinkedList<>();
				previousGroup = ZoneGroup.UNKNOWN;
			}
		}

		if (currentChain.size() > 0) {
			chains.add(currentChain);
		}

		return chains;
	}

	public double computeCost(List<Coord> coords, boolean halfFare) {
		List<Optional<Station>> longDistanceStations = new LinkedList<>();
		Set<Integer> indicesWithLongDistanceStations = new HashSet<>();

		List<Optional<Zone>> zones = new LinkedList<>();
		Set<Integer> indicesWithZones = new HashSet<>();

		// List<Optional<Zone>> zones = new LinkedList<>();
		/*
		 * for (Coord coord : coords) { zones.add(zonalSystem.getZone(coord)); }
		 */

		for (int index = 0; index < coords.size(); index++) {
			SpatialStation closestStation = spatialT603.getClosestStation(coords.get(index));

			if (CoordUtils.calcEuclideanDistance(coords.get(index), closestStation.coord) < 250.0) {
				longDistanceStations.add(Optional.of(closestStation.station));
				indicesWithLongDistanceStations.add(index);
			} else {
				longDistanceStations.add(Optional.empty());
			}

			Optional<Zone> zone = zonalSystem.getZone(coords.get(index));
			zones.add(zone);

			if (zone.isPresent()) {
				indicesWithZones.add(index);
			}
		}

		// First, find connected long distance chains
		List<List<Integer>> longDistanceChains = findConsecutiveLongDistanceIndices(coords.size(),
				indicesWithLongDistanceStations);

		// If a "long distance" is completely covered by zones, do not consider them as
		// long distance!
		Iterator<List<Integer>> chainIterator = longDistanceChains.iterator();
		Set<Integer> framedLongDistanceIndices = new HashSet<>();

		while (chainIterator.hasNext()) {
			List<Integer> currentChain = chainIterator.next();

			Set<ZoneGroup> groups = new HashSet<>();
			boolean unknownZoneFound = false;

			for (int i = 0; i < currentChain.size(); i++) {
				Optional<Zone> zone = zones.get(currentChain.get(i));

				if (zone.isPresent()) {
					groups.add(zone.get().getGroup());
				} else {
					unknownZoneFound = true;
				}
			}

			if (!unknownZoneFound && groups.size() == 1) {
				chainIterator.remove();
			} else {
				for (int i = 1; i < currentChain.size() - 1; i++) {
					framedLongDistanceIndices.add(currentChain.get(i));
				}
			}
		}

		// The indices which are now framed in a long distance trip will not be counted
		// as zonal indices anymore:
		indicesWithZones.removeAll(framedLongDistanceIndices);

		// Second, find consecutive chains with a zone
		List<List<Integer>> zonalChains = findConsecutiveZonalIndices(coords.size(), indicesWithZones, zones);
		chainIterator = zonalChains.iterator();

		while (chainIterator.hasNext()) {
			List<Integer> currentChain = chainIterator.next();

			if (currentChain.size() < 2) {
				chainIterator.remove();
			}
		}

		// At this point we have two lists:
		// 1) longDistanceChains contains chains that can be calculated using T603 for
		// long distance connections
		// 2) zonalChains contains chains that can be calculated using the zonal system.
		// Note that they do not need to belong to a single zone. One chain here can
		// also go through multiple zones as long as they are connected.
		// 3) there maybe indices which are not included in any of those. They should be
		// treated differently

		double price = 0.0;
		Set<Integer> coveredIndices = new HashSet<>();

		for (List<Integer> chain : longDistanceChains) {
			price += computeLongDistancePrice(longDistanceStations, chain, halfFare);
			coveredIndices.addAll(chain);
		}

		for (List<Integer> chain : zonalChains) {
			price += computeZonalPrice(zones, chain, halfFare);
			coveredIndices.addAll(chain);
		}

		for (int index = 1; index < coords.size(); index++) {
			if (!coveredIndices.contains(index) || !coveredIndices.contains(index - 1)) {
				double uncoveredDistance = CoordUtils.calcEuclideanDistance(coords.get(index - 1), coords.get(index));
				price += computeUncoveredPrice(uncoveredDistance * 1e-3, halfFare);
			}
		}

		return price;
	}

	private double computeLongDistancePrice(List<Optional<Station>> stations, List<Integer> indices, boolean halfFare) {
		double distance = 0.0;
		
		//Station originStation = stations.get(indices.get(0)).get();
		//Station destinationStation = stations.get(indices.get(indices.size() - 1)).get();
		//double distance = t603.computeDistance(originStation, destinationStation);
		
		for (int i = 1; i < indices.size(); i++) {
			Station originStation = stations.get(indices.get(i - 1)).get();
			Station destinationStation = stations.get(indices.get(i)).get();
			distance += t603.computeDistance(originStation, destinationStation);
		}
		
		double price = distance > 0.0 ? t603.computePrice(distance, halfFare) : 0.0;
		//System.out.println("long distance: " + price);
		return price;
	}

	private double computeZonalPrice(List<Optional<Zone>> zones, List<Integer> indices, boolean halfFare) {
		List<Zone> chainZones = new LinkedList<>();

		for (int i = 0; i < indices.size(); i++) {
			chainZones.add(zones.get(indices.get(i)).get());
		}

		double price = zonalSystem.getPrice(chainZones, halfFare);
		//System.out.println("zonal: " + price);
		return price;
	}

	private double computeUncoveredPrice(double distance, boolean halfFare) {
		double price = distance * 0.25;
		
		if (halfFare) {
			price *= 0.5;
		}
		
		price *= 10.0;
		price = Math.ceil(price);
		price *= 0.1;
		
		//System.out.println("uncovered: " + price + " " + distance);
		return price;
	}
}
