package ch.ethz.matsim.ch_pt_utils.routing.batch;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.routing.request.PlanRoutingRequest;
import ch.ethz.matsim.ch_pt_utils.routing.request.TripRoutingRequest;

public class PlanRoutingRequestIterator implements Iterator<PlanRoutingRequest> {
	private final Iterator<TripRoutingRequest> delegate;
	private TripRoutingRequest pendingRequest;

	public PlanRoutingRequestIterator(Iterator<TripRoutingRequest> delegate) {
		this.delegate = delegate;
	}

	@Override
	public PlanRoutingRequest next() {
		List<TripRoutingRequest> currentRequests = new LinkedList<>();

		if (pendingRequest != null) {
			currentRequests.add(pendingRequest);
			pendingRequest = null;
		}

		while (delegate.hasNext()) {
			TripRoutingRequest next = delegate.next();
			String planId = currentRequests.size() == 0 ? null : currentRequests.get(0).getPlanId();

			if (planId == null || next.getPlanId().equals(planId)) {
				currentRequests.add(next);
			} else {
				pendingRequest = next;
				break;
			}
		}

		return new PlanRoutingRequest(currentRequests.get(0).getPlanId(), currentRequests);
	}

	@Override
	public boolean hasNext() {
		return delegate.hasNext() || pendingRequest != null;
	}
}
