package ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.matsim.core.utils.collections.Tuple;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.TicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.filter.TrajectoryStageFilter;

public class TrajectoryTicketAdapter implements TicketGenerator {
	private final TrajectoryStageFilter filter;
	private final TrajectoryTicketGenerator generator;

	public TrajectoryTicketAdapter(TrajectoryStageFilter filter, TrajectoryTicketGenerator generator) {
		this.filter = filter;
		this.generator = generator;
	}

	private List<Tuple<Integer, Integer>> findMaximumLengthIndices(List<TransitStage> stages) {
		List<Tuple<Integer, Integer>> indices = new LinkedList<>();

		int startIndex = -1;

		for (int stageIndex = 0; stageIndex < stages.size(); stageIndex++) {
			TransitStage stage = stages.get(stageIndex);

			if (filter.isRelevant(stage)) {
				if (startIndex == -1) {
					startIndex = stageIndex;
				}
			} else {
				if (startIndex != -1) {
					indices.add(new Tuple<>(startIndex, stageIndex));
					startIndex = -1;
				}
			}
		}

		if (startIndex != -1) {
			indices.add(new Tuple<>(startIndex, stages.size()));
		}

		return indices;
	}

	private List<Tuple<Integer, Integer>> findIndices(List<TransitStage> stages) {
		List<Tuple<Integer, Integer>> maximumLengthIndices = findMaximumLengthIndices(stages);
		List<Tuple<Integer, Integer>> indices = new LinkedList<>();

		for (Tuple<Integer, Integer> index : maximumLengthIndices) {
			int startIndex = index.getFirst();
			int endIndex = index.getSecond();

			int maximumLength = endIndex - startIndex;

			for (int stride = 1; stride <= maximumLength; stride++) {
				for (int offset = 0; offset <= maximumLength - stride; offset++) {
					int itemStartIndex = startIndex + offset;
					int itemEndIndex = itemStartIndex + stride;

					indices.add(new Tuple<>(itemStartIndex, itemEndIndex));
				}
			}
		}

		return indices;
	}

	@Override
	public Collection<Ticket> createTickets(List<TransitStage> stages, boolean halfFare) {
		List<Ticket> tickets = new LinkedList<>();
		List<Tuple<Integer, Integer>> railTripIndices = findIndices(stages);

		for (Tuple<Integer, Integer> tripIndex : railTripIndices) {
			int startIndex = tripIndex.getFirst();
			int endIndex = tripIndex.getSecond();

			tickets.addAll(
					generator.createTickets(stages.subList(startIndex, endIndex), startIndex, stages.size(), halfFare));
		}

		return tickets;
	}
}
