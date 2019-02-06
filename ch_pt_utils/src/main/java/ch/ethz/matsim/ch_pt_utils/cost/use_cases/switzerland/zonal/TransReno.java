package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal;

import java.util.Collection;
import java.util.HashSet;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.TicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.DistanceBasedTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.TrajectoryTicketAdapter;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.filter.TrajectoryStageFilter;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.filter.ZoneTrajectoryFilter;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.SingleZoneTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb.SBBTicketGenerator;

public class TransReno {
	private TransReno() {
	}

	static public ZonalTicketGenerator createTicketGenerator(ZonalRegistry zonalRegistry) {
		Authority authority = zonalRegistry.getAuthority("TransReno");
		Zone zone1 = zonalRegistry.getZone(authority, 1);
		return new SingleZoneTicketGenerator("Chur", zone1, 3.0, 2.2, 3600.0, 6.0, 4.4);
	}

	static public TicketGenerator createTrajectoryTicketGenerator(ZonalRegistry zonalRegistry) {
		Authority authority = zonalRegistry.getAuthority("TransReno");
		Collection<Zone> zones = new HashSet<>();

		for (Zone zone : zonalRegistry.getZones(authority)) {
			if (zone.getZoneId() != 1) {
				zones.add(zone);
			}
		}

		TrajectoryStageFilter filter = new ZoneTrajectoryFilter(zonalRegistry, zones);
		return new TrajectoryTicketAdapter(filter,
				new DistanceBasedTicketGenerator(authority, TransReno::calculateDistancePrice));
	}

	static double calculateDistancePrice(double distance, boolean halfFare) {
		double distanceKm = 1e-3 * distance;
		double factor = halfFare ? 0.5 : 1.0;
		return factor * SBBTicketGenerator.calculateFullCost(distanceKm);
	}
}
