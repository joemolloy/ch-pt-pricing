package ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalStage;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalWaypoint;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Zone;

public class SingleZoneTicketGenerator implements ZonalTicketGenerator {
	private final String name;
	private final Zone zone;

	private final double singleFullFarePrice;
	private final double singleHalfFarePrice;

	private final double dayFullFarePrice;
	private final double dayHalfFarePrice;

	private final double singleValidity;

	public SingleZoneTicketGenerator(String name, Zone zone, double singleFullFarePrice, double singleHalfFarePrice,
			double singleValidity, double dayFullFarePrice, double dayHalfFarePrice) {
		this.name = name;
		this.zone = zone;
		this.singleFullFarePrice = singleFullFarePrice;
		this.singleHalfFarePrice = singleHalfFarePrice;
		this.dayFullFarePrice = dayFullFarePrice;
		this.dayHalfFarePrice = dayHalfFarePrice;
		this.singleValidity = singleValidity;
	}

	private String createName(String type) {
		return String.format("%s %s %s", zone.getAuthority().getId(), type, name);
	}

	private double getSingleFare(boolean halfFare) {
		return halfFare ? singleHalfFarePrice : singleFullFarePrice;
	}

	private double getDayFare(boolean halfFare) {
		return halfFare ? dayHalfFarePrice : dayFullFarePrice;
	}

	private boolean isCovered(ZonalStage stage) {
		if (stage.getWaypoints().size() == 0) {
			return false;
		}
		
		for (ZonalWaypoint waypoint : stage.getWaypoints()) {
			if (!waypoint.getZones().contains(zone)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public Collection<Ticket> createTickets(List<ZonalStage> stages, boolean halfFare) {
		LinkedList<Ticket> tickets = new LinkedList<>();

		for (int stageIndex = 0; stageIndex < stages.size(); stageIndex++) {
			ZonalStage currentStage = stages.get(stageIndex);

			if (isCovered(currentStage)) {
				Ticket singleTicket = new Ticket(stages.size(), getSingleFare(halfFare), createName("Single"));
				Ticket dayTicket = new Ticket(stages.size(), getDayFare(halfFare), createName("Day"));
				double validUntil = currentStage.getDepartureTime() + singleValidity;

				for (int futureIndex = stageIndex; futureIndex < stages.size(); futureIndex++) {
					ZonalStage futureStage = stages.get(futureIndex);

					if (isCovered(futureStage)) {
						dayTicket.getCoverage().set(futureIndex);

						if (futureStage.getArrivalTime() <= validUntil) {
							singleTicket.getCoverage().set(futureIndex);
						}
					}
				}

				if (singleTicket.getCoverage().cardinality() > 0) {
					tickets.add(singleTicket);
				}

				if (dayTicket.getCoverage().cardinality() > singleTicket.getCoverage().cardinality()) {
					tickets.add(dayTicket);
				}
			}
		}

		return tickets;
	}

}
