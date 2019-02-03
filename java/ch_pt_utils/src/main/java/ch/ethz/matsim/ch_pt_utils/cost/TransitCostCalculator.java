package ch.ethz.matsim.ch_pt_utils.cost;

import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.TicketSolver;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;

public interface TransitCostCalculator {
	TicketSolver.Result computeCost(List<TransitStage> stages, boolean halfFare);
}