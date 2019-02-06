package ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.TicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalStage;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalWaypoint;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Zone;

public class ZonalTicketAdapter implements TicketGenerator {
	private final ZonalTicketGenerator zonalTicketGenerator;
	private final ZonalRegistry zonalRegistry;

	public ZonalTicketAdapter(ZonalTicketGenerator zonalTicketGenerator, ZonalRegistry zonalRegistry) {
		this.zonalTicketGenerator = zonalTicketGenerator;
		this.zonalRegistry = zonalRegistry;
	}

	@Override
	public Collection<Ticket> createTickets(List<TransitStage> stages, boolean halfFare) {
		List<ZonalStage> zonalStages = new ArrayList<>(stages.size());

		for (TransitStage stage : stages) {
			ZonalStage zonalStage = new ZonalStage(stage.getDepartureTime(), stage.getArrivalTime());
			boolean skip = false;

			for (long hafasId : stage.getHafasIds()) {
				Collection<Zone> waypointZones = zonalRegistry.getZones(hafasId);

				if (waypointZones.size() > 0) {
					zonalStage.addWaypoint(new ZonalWaypoint(waypointZones));
				} else {
					// Do not consider a stage in which one waypoint is not covered by a zone
					skip = true;
					break;
				}
			}

			if (!skip) {
				zonalStages.add(zonalStage);
			}
		}

		return zonalTicketGenerator.createTickets(zonalStages, halfFare);
	}
}
