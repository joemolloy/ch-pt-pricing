package ch.ethz.matsim.ch_pt_utils.routing.batch;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.routing.PlanRoutingRequest;
import ch.ethz.matsim.ch_pt_utils.routing.TripRoutingRequest;

public class PlanRoutingRequestIterator implements Iterator<PlanRoutingRequest> {
	private final Iterator<TripRoutingRequest> delegate;
	private TripRoutingRequest head = null;

	public PlanRoutingRequestIterator(Iterator<TripRoutingRequest> delegate) {
		this.delegate = delegate;
	}

	@Override
	public PlanRoutingRequest next() {
		if (head == null) {
			head = delegate.next();
		}

		List<TripRoutingRequest> trips = new LinkedList<>();
		trips.add(head);

		TripRoutingRequest newHead = null;

		while (delegate.hasNext()) {
			TripRoutingRequest next = delegate.next();

			if (next.getPlanId().equals(head.getPlanId())) {
				trips.add(next);
			} else {
				newHead = next;
				break;
			}
		}

		head = newHead;
		return new PlanRoutingRequest(trips.get(0).getPlanId(), trips);
	}

	@Override
	public boolean hasNext() {
		return delegate.hasNext() || head != null;
	}
}
