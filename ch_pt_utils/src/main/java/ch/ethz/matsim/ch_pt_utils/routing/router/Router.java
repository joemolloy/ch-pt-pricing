package ch.ethz.matsim.ch_pt_utils.routing.router;

import ch.ethz.matsim.ch_pt_utils.routing.PlanRoutingRequest;
import ch.ethz.matsim.ch_pt_utils.routing.PlanRoutingResult;

public interface Router {
	PlanRoutingResult process(PlanRoutingRequest planRequest);
}
