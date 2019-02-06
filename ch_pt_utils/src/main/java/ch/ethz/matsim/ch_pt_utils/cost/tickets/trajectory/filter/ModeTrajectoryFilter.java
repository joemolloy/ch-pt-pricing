package ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.filter;

import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;

public class ModeTrajectoryFilter implements TrajectoryStageFilter {
	private final String mode;

	public ModeTrajectoryFilter(String mode) {
		this.mode = mode;
	}

	@Override
	public boolean isRelevant(TransitStage stage) {
		return stage.getMode().equals(mode);
	}
}
