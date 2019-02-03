package ch.ethz.matsim.ch_pt_utils.routing.router;

import ch.ethz.matsim.ch_pt_utils.routing.RoutingRequest;
import ch.ethz.matsim.ch_pt_utils.routing.RoutingResult;

public interface Router {
	RoutingResult process(RoutingRequest request);
}
