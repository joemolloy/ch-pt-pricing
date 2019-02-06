package ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Authority;

public class DistanceBasedTicketGenerator implements TrajectoryTicketGenerator {
	private final DistancePriceCalculator calculator;
	private final Authority authority;

	public DistanceBasedTicketGenerator(Authority authority, DistancePriceCalculator calculator) {
		this.authority = authority;
		this.calculator = calculator;
	}

	private String createName(double distance) {
		double distanceKm = Math.ceil(distance * 1e-3);
		return String.format("%s Single %dkm", authority.getId(), (int) distanceKm);
	}

	@Override
	public Collection<Ticket> createTickets(List<TransitStage> trajectory, int startIndex, int numberOfStages,
			boolean halfFare) {
		double distance = 0.0;

		for (TransitStage stage : trajectory) {
			distance += stage.getDistance();
		}

		double price = calculator.calculatePrice(distance, halfFare);
		Ticket ticket = new Ticket(numberOfStages, price, createName(distance));

		for (int offset = 0; offset < trajectory.size(); offset++) {
			ticket.getCoverage().set(startIndex + offset);
		}

		if (ticket.getCoverage().cardinality() > 0) {
			return Collections.singleton(ticket);
		} else {
			return Collections.emptyList();
		}
	}

	static public interface DistancePriceCalculator {
		double calculatePrice(double distance, boolean halfFare);
	}
}
