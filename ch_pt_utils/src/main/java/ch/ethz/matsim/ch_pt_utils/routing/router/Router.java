package ch.ethz.matsim.ch_pt_utils.routing.router;

import ch.ethz.matsim.ch_pt_utils.routing.request.PlanRoutingRequest;
import ch.ethz.matsim.ch_pt_utils.routing.result.PlanRoutingResult;

public interface Router {
	PlanRoutingResult process(PlanRoutingRequest planRequest);
}
