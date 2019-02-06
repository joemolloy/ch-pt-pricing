package ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.filter;

import java.util.Collection;

import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Zone;

public class ZoneTrajectoryFilter implements TrajectoryStageFilter {
	private final Collection<Zone> zones;
	private final ZonalRegistry zonalRegistry;

	public ZoneTrajectoryFilter(ZonalRegistry zonalRegistry, Collection<Zone> zones) {
		this.zones = zones;
		this.zonalRegistry = zonalRegistry;
	}

	@Override
	public boolean isRelevant(TransitStage stage) {
		if (stage.getHafasIds().size() == 0) {
			return false;
		}

		for (long hafasId : stage.getHafasIds()) {
			boolean isValid = false;

			for (Zone zone : zonalRegistry.getZones(hafasId)) {
				if (zones.contains(zone)) {
					isValid = true;
					break;
				}
			}

			if (!isValid) {
				return false;
			}
		}

		return true;
	}
}
