package ch.ethz.matsim.ch_pt_utils.cost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.sbb.RailTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.solver.TicketSolver;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalStage;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalWaypoint;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.Zone;

public class SwissTransitCostCalculator implements TransitCostCalculator {
	private final ZonalRegistry zoneRegistry;
	private final ZonalTicketGenerator zonalTicketGenerator;
	private final RailTicketGenerator railTicketGenerator;

	public SwissTransitCostCalculator(ZonalRegistry zoneRegistry, ZonalTicketGenerator zonalTicketGenerator,
			RailTicketGenerator railTicketGenerator) {
		this.zoneRegistry = zoneRegistry;
		this.zonalTicketGenerator = zonalTicketGenerator;
		this.railTicketGenerator = railTicketGenerator;
	}

	/* (non-Javadoc)
	 * @see ch.ethz.matsim.ch_pt_utils.cost.TransitCostCalculator#computeCost(java.util.List, boolean)
	 */
	@Override
	public TicketSolver.Result computeCost(List<TransitStage> stages, boolean halfFare) {
		List<Ticket> tickets = new LinkedList<>();

		// First, create zonal tickets

		List<ZonalStage> zonalStages = new ArrayList<>(stages.size());

		for (TransitStage stage : stages) {
			ZonalStage zonalStage = new ZonalStage(stage.getDepartureTime(), stage.getArrivalTime());

			for (long hafasId : stage.getHafasIds()) {
				Collection<Zone> waypointZones = zoneRegistry.getZones(hafasId);

				if (waypointZones.size() > 0) {
					zonalStage.addWaypoint(new ZonalWaypoint(waypointZones));
				} else {
					// Do not consider a stage in which one waypoint is not covered by a zone
					continue;
				}
			}

			zonalStages.add(zonalStage);
		}

		tickets.addAll(zonalTicketGenerator.createTickets(zonalStages, halfFare));

		// Second, create rail tickets
		tickets.addAll(railTicketGenerator.createTickets(stages));

		// Third, create fallback tickets
		for (int i = 0; i < stages.size(); i++) {
			int coveringTickets = 0;

			for (Ticket ticket : tickets) {
				if (ticket.getCoverage().get(i)) {
					coveringTickets++;
				}
			}

			if (coveringTickets == 0) {
				double cost = calculateFallbackCost(stages.get(i).getDistance());
				Ticket ticket = new Ticket(stages.size(), cost, "Fallback");
				ticket.getCoverage().set(i);
				tickets.add(ticket);
			}
		}

		// Find minimum combination
		return new TicketSolver().solve(stages.size(), tickets);
	}

	private double calculateFallbackCost(double distance) {
		return distance * 1e-3 * 0.25;
	}
}
