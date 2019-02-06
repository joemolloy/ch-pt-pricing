package ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.filter;

import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;

public interface TrajectoryStageFilter {
	boolean isRelevant(TransitStage stage);
}
