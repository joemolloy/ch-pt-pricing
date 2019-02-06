package ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.ZonalTicketCalculator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalStage;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalWaypoint;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Zone;

public class DefaultZonalTicketGenerator implements ZonalTicketGenerator {
	private final Authority authority;
	private final ZonalTicketCalculator calculator;

	public DefaultZonalTicketGenerator(Authority authority, ZonalTicketCalculator calculator) {
		this.authority = authority;
		this.calculator = calculator;
	}

	/**
	 * TODO: This could be revised. The tricky part is there where one station has
	 * multiple zones of the same authority. The way it is implemented right now,
	 * the agent tries to stay as long as possible in the same zone.
	 */
	private boolean findRelevantZones(ZonalStage stage, Collection<Zone> relevantZones) {
		if (stage.getWaypoints().size() == 0) {
			return false;
		}

		List<Zone> localRelevantZones = new LinkedList<>();

		for (ZonalWaypoint waypoint : stage.getWaypoints()) {
			Collection<Zone> relevantWaypointZones = new HashSet<>();

			for (Zone zone : waypoint.getZones()) {
				if (zone.getAuthority().equals(authority)) {
					relevantWaypointZones.add(zone);
				}
			}

			if (relevantWaypointZones.size() == 0) {
				return false;
			} else {
				if (localRelevantZones.size() == 0
						|| !relevantWaypointZones.contains(localRelevantZones.get(localRelevantZones.size() - 1))) {
					localRelevantZones.add(relevantWaypointZones.iterator().next());
				}
			}
		}

		if (localRelevantZones.size() == 0) {
			return false;
		} else {
			relevantZones.addAll(localRelevantZones);
			return true;
		}
	}

	private String createName(String type, Set<Zone> zones) {
		String zoneString = String.join(", ",
				zones.stream().sorted().map(Zone::getZoneId).map(String::valueOf).collect(Collectors.toList()));
		return authority.getId() + " " + type + " " + zoneString;
	}

	@Override
	public Collection<Ticket> createTickets(List<ZonalStage> stages, boolean halfFare) {
		LinkedList<Ticket> tickets = new LinkedList<>();

		for (int stageIndex = 0; stageIndex < stages.size(); stageIndex++) {
			ZonalStage currentStage = stages.get(stageIndex);

			for (int offset = 1; offset < stages.size() - stageIndex + 1; offset++) {
				Set<Zone> futureZones = new HashSet<>();
				Set<Integer> coveredIndices = new HashSet<>();

				for (int futureStageIndex = stageIndex; futureStageIndex < stageIndex + offset; futureStageIndex++) {
					ZonalStage futureStage = stages.get(futureStageIndex);

					if (findRelevantZones(futureStage, futureZones)) {
						coveredIndices.add(futureStageIndex);
					}
				}

				if (coveredIndices.size() > 0) {
					double singlePrice = calculator.calculateSingleTicketPrice(futureZones, halfFare);
					Ticket singleTicket = new Ticket(stages.size(), singlePrice, createName("Single", futureZones));
					double validUntil = currentStage.getDepartureTime() + calculator.calculateValidity(futureZones);

					double dayPrice = calculator.calculateDayTicketPrice(futureZones, halfFare);
					Ticket dayTicket = new Ticket(stages.size(), dayPrice, createName("Day", futureZones));

					for (int futureStageIndex : coveredIndices) {
						ZonalStage futureStage = stages.get(futureStageIndex);

						if (futureStage.getArrivalTime() <= validUntil) {
							singleTicket.getCoverage().set(futureStageIndex);
						}

						dayTicket.getCoverage().set(futureStageIndex);
					}

					if (singleTicket.getCoverage().cardinality() > 0) {
						tickets.add(singleTicket);
					}

					if (dayTicket.getCoverage().cardinality() > 0) {
						tickets.add(dayTicket);
					}
				}
			}
		}

		return tickets;
	}

}
