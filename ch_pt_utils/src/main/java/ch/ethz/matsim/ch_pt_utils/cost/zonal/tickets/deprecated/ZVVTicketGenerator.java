package ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.deprecated;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalStage;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalWaypoint;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Zone;

public class ZVVTicketGenerator implements ZonalTicketGenerator {
	private final Authority authority;
	private final Zone zone110;
	private final Zone zone120;

	public ZVVTicketGenerator(Authority authority, ZonalRegistry zoneRegistry) {
		this.authority = authority;

		Zone zone110 = null;
		Zone zone120 = null;

		for (Zone zone : zoneRegistry.getZones(authority)) {
			if (zone.getAuthority().equals(authority)) {
				if (zone.getZoneId() == 110) {
					zone110 = zone;
				}

				if (zone.getZoneId() == 120) {
					zone120 = zone;
				}
			}
		}

		if (zone110 == null) {
			throw new IllegalStateException();
		}

		if (zone120 == null) {
			throw new IllegalStateException();
		}

		this.zone110 = zone110;
		this.zone120 = zone120;
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
					int numberOfZones = ticketZones.size();

					if (ticketZones.contains(zone110)) {
						numberOfZones++;
					}

					if (ticketZones.contains(zone120)) {
						numberOfZones++;
					}

					String zonesAsString = String.join(", ", ticketZones.stream().sorted()
							.map(z -> String.valueOf(z.getZoneId())).collect(Collectors.toList()));

					String singleTicketName = String.format("Single ZVV (%s)", zonesAsString);
					String dayTicketName = String.format("Day ZVV (%s)", zonesAsString);

					Ticket singleTicket = new Ticket(stages.size(), getSingleTicketPrice(numberOfZones),
							singleTicketName);
					Ticket dayTicket = new Ticket(stages.size(), getDayTicketPrice(numberOfZones), dayTicketName);

					double maximumValidity = getSingleTicketValidity(numberOfZones) + stage.getDepartureTime();

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

	private List<Zone> getLookaheadZones(List<ZonalStage> stages, int startIndex) {
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

	private List<Double> SINGLE_TICKET_PRICES = Arrays.asList(2.7, 4.4, 4.4, 6.8, 8.8, 10.8, 13.0, 15.0, 17.2);
	private List<Double> SINGLE_TICKET_VALIDTY = Arrays.asList(0.5, 1.0, 1.0, 2.0, 2.0, 2.0, 2.0, 2.0);
	private List<Double> DAY_TICKET_PRICES = Arrays.asList(5.4, 8.8, 8.8, 13.6, 17.6, 21.6, 26.0, 30.0, 34.4);

	private double getSingleTicketPrice(int numberOfZones) {
		return SINGLE_TICKET_PRICES.get((int) numberOfZones);
	}

	private double getSingleTicketValidity(int numberOfZones) {
		return SINGLE_TICKET_VALIDTY.get((int) numberOfZones) * 3600.0;
	}

	private double getDayTicketPrice(int numberOfZones) {
		return DAY_TICKET_PRICES.get((int) numberOfZones);
	}
}
