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

	public SBBTicketGenerator(TriangleRegistry triangleRegistry, ZonalRegistry zonalRegistry) {
		this.zonalRegistry = zonalRegistry;
		this.triangleRegistry = triangleRegistry;
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
				double cost = DistanceTariff.calculateCost(distance);

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
}
