package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.TrajectoryTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb.data.Triangle;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb.data.TriangleRegistry;

public class SBBTicketGenerator implements TrajectoryTicketGenerator {
	private final TriangleRegistry triangleRegistry;
	private final ZonalRegistry zonalRegistry;
	private final static double[] costTable = createCostTable();

	public SBBTicketGenerator(TriangleRegistry triangleRegistry, ZonalRegistry zonalRegistry) {
		this.zonalRegistry = zonalRegistry;
		this.triangleRegistry = triangleRegistry;
	}

	public static double calculateFullCost(double distance) {
		int intDistance = (int) distance;
		return costTable[intDistance];
	}

	private Optional<Double> computeStageDistance(TransitStage stage) {
		List<Long> hafasIds = stage.getHafasIds();
		double totalDistance = 0.0;

		for (int i = 1; i < hafasIds.size(); i++) {
			long originHafasId = hafasIds.get(i - 1);
			long destinationHafasId = hafasIds.get(i);

			Collection<Triangle> directTriangles = triangleRegistry.getDirectTriangles(originHafasId,
					destinationHafasId);

			if (directTriangles.size() > 0) {
				double minimumDistance = Double.POSITIVE_INFINITY;

				for (Triangle directTriangle : directTriangles) {
					double distance = directTriangle.getDistance(originHafasId, destinationHafasId);

					if (distance < minimumDistance) {
						minimumDistance = distance;
					}
				}

				totalDistance += minimumDistance;
			} else {
				Collection<Triangle> originTriangles = triangleRegistry.getTriangles(originHafasId);
				Collection<Triangle> destinationTriangles = triangleRegistry.getTriangles(destinationHafasId);

				double minimumDistance = Double.POSITIVE_INFINITY;

				for (Triangle originTriangle : originTriangles) {
					for (Triangle destinationTriangle : destinationTriangles) {
						for (long connectionId : triangleRegistry.getConnectingIds(originTriangle,
								destinationTriangle)) {
							double distance = originTriangle.getDistance(originHafasId, connectionId)
									+ destinationTriangle.getDistance(connectionId, destinationHafasId);

							if (distance < minimumDistance) {
								minimumDistance = distance;
							}
						}
					}
				}

				if (!Double.isFinite(minimumDistance)) {
					return Optional.empty();
				} else {
					totalDistance += minimumDistance;
				}
			}
		}

		return Optional.of(totalDistance);
	}

	@Override
	public Collection<Ticket> createTickets(List<TransitStage> trajectory, int startIndex, int numberOfStages,
			boolean halfFare) {
		List<Ticket> tickets = new LinkedList<>();

		double distance = 0.0;
		boolean validDistanceFound = true;
		List<Set<Authority>> authorities = new ArrayList<>(2 * trajectory.size());

		for (TransitStage stage : trajectory) {
			long originHafasId = stage.getHafasIds().get(0);
			long destinationHafasId = stage.getHafasIds().get(stage.getHafasIds().size() - 1);

			Optional<Double> stageDistance = computeStageDistance(stage);

			if (stageDistance.isPresent()) {
				distance += stageDistance.get();
			} else {
				validDistanceFound = false;
				continue;
			}

			authorities.add(
					zonalRegistry.getZones(originHafasId).stream().map(Zone::getAuthority).collect(Collectors.toSet()));
			authorities.add(zonalRegistry.getZones(destinationHafasId).stream().map(Zone::getAuthority)
					.collect(Collectors.toSet()));
		}

		if (validDistanceFound) {
			Set<Authority> uniqueAuthorities = new HashSet<>();
			authorities.forEach(uniqueAuthorities::addAll);
			boolean coveredByAuthority = false;

			for (Authority authority : uniqueAuthorities) {
				if (authorities.stream().filter(l -> !l.contains(authority)).count() == 0) {
					coveredByAuthority = true;
					break;
				}
			}

			if (!coveredByAuthority) {
				double cost = calculateFullCost(distance);

				List<Long> stringTicketList = new LinkedList<>();

				for (TransitStage stage : trajectory) {

					long originHafasId = stage.getHafasIds().get(0);
					long destinationHafasId = stage.getHafasIds().get(stage.getHafasIds().size() - 1);

					if (stringTicketList.size() == 0) {
						stringTicketList.add(originHafasId);
					} else if (!stringTicketList.get(stringTicketList.size() - 1).equals(originHafasId)) {
						stringTicketList.add(originHafasId);
					}

					if (!stringTicketList.get(stringTicketList.size() - 1).equals(destinationHafasId)) {
						stringTicketList.add(destinationHafasId);
					}
				}

				String stringTicket = "SBB " + String.join(", ",
						stringTicketList.stream().map(String::valueOf).collect(Collectors.toList()));

				Ticket ticket = new Ticket(numberOfStages, cost, stringTicket);

				for (int offset = 0; offset < trajectory.size(); offset++) {
					ticket.getCoverage().set(startIndex + offset);
				}

				tickets.add(ticket);
			}
		}

		return tickets;
	}

	private static double[] createCostTable() {
		double[] costTable = new double[1500];

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

			double cost = 0.0;

			for (int currentDistance = 1; currentDistance < distance + 1; currentDistance++) {
				if (currentDistance <= 4) {
					cost += 44.51;
				} else if (currentDistance <= 14) {
					cost += 42.30;
				} else if (currentDistance <= 48) {
					cost += 37.24;
				} else if (currentDistance <= 150) {
					cost += 26.46;
				} else if (currentDistance <= 200) {
					cost += 25.71;
				} else if (currentDistance <= 250) {
					cost += 22.85;
				} else if (currentDistance <= 300) {
					cost += 20.63;
				} else if (currentDistance <= 480) {
					cost += 20.09;
				} else {
					cost += 19.85;
				}
			}

			// Prices for distances <70km are rounded to 20 Rp, prices above to 1 Fr

			if (distance <= 69) {
				cost = Math.ceil(cost / 20.0) * 20.0;
			} else {
				cost = Math.ceil(cost / 100.0) * 100.0;
			}

			// The minimum price is 3 Fr

			cost = Math.max(cost, 300.0);
			costTable[distanceIndex] = cost / 100.0;
		}

		return costTable;
	}
}
