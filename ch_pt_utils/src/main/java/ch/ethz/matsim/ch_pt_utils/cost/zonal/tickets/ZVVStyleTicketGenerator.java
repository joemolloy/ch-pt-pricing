package ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalStage;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalWaypoint;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Zone;

public class ZVVStyleTicketGenerator implements ZonalTicketGenerator {
	private final Authority authority;
	private final Collection<Zone> doubleZones;
	private final ZVVStylePricing pricing;

	public ZVVStyleTicketGenerator(Authority authority, Collection<Zone> doubleZones, ZVVStylePricing pricing) {
		this.authority = authority;
		this.doubleZones = doubleZones;
		this.pricing = pricing;
	}

	private int countZones(List<Zone> ticketZones) {
		int numberOfZones = ticketZones.size();

		for (Zone doubleZone : doubleZones) {
			if (ticketZones.contains(doubleZone)) {
				numberOfZones++;
			}
		}

		return numberOfZones;
	}

	@Override
	public Collection<Ticket> createTickets(List<ZonalStage> stages, boolean halfFare) {
		List<Ticket> tickets = new LinkedList<>();

		for (int stageIndex = 0; stageIndex < stages.size(); stageIndex++) {
			List<Zone> lookaheadZones = getLookaheadZones(stages, stageIndex);
			ZonalStage stage = stages.get(stageIndex);

			if (isRelevant(stage)) {

				for (int futureIndex = 1; futureIndex <= lookaheadZones.size(); futureIndex++) {
					List<Zone> ticketZones = lookaheadZones.subList(0, futureIndex);
					int numberOfZones = countZones(ticketZones);

					String zonesAsString = String.join(", ", ticketZones.stream().sorted()
							.map(z -> String.valueOf(z.getZoneId())).collect(Collectors.toList()));

					String singleTicketName = String.format("Single %s (%s)", authority.getId(), zonesAsString);
					String dayTicketName = String.format("Day %s (%s)", authority.getId(), zonesAsString);

					Ticket singleTicket = new Ticket(stages.size(),
							pricing.getSingleTicketPrice(numberOfZones, halfFare), singleTicketName);
					Ticket dayTicket = new Ticket(stages.size(), pricing.getDayTicketPrice(numberOfZones, halfFare),
							dayTicketName);

					double maximumValidity = pricing.getSingleTicketValidity(numberOfZones) + stage.getDepartureTime();

					for (int k = stageIndex; k < stages.size(); k++) {
						ZonalStage futureStage = stages.get(k);
						singleTicket.getCoverage().set(k,
								futureStage.isCovered(ticketZones) && futureStage.isCovered(maximumValidity));
						dayTicket.getCoverage().set(k, futureStage.isCovered(ticketZones));
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

	private boolean isRelevant(ZonalStage stage) {
		for (ZonalWaypoint waypoint : stage.getWaypoints()) {
			for (Zone zone : waypoint.getZones()) {
				if (zone.getAuthority().equals(authority)) {
					return true;
				}
			}
		}

		return false;
	}

	protected List<Zone> getLookaheadZones(List<ZonalStage> stages, int startIndex) {
		List<Zone> lookaheadZones = new LinkedList<>();

		for (int lookaheadIndex = startIndex; lookaheadIndex < stages.size(); lookaheadIndex++) {
			ZonalStage lookaheadStage = stages.get(lookaheadIndex);

			for (ZonalWaypoint waypoint : lookaheadStage.getWaypoints()) {
				for (Zone zone : waypoint.getZones()) {
					if (zone.getAuthority().equals(authority) && !lookaheadZones.contains(zone)) {
						lookaheadZones.add(zone);
					}
				}
			}
		}

		return lookaheadZones;
	}
}
