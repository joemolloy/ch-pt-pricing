package ch.ethz.matsim.ch_pt_utils.routing.batch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.function.Consumer;

import ch.ethz.matsim.ch_pt_utils.routing.result.PlanRoutingResult;
import ch.ethz.matsim.ch_pt_utils.routing.result.TripRoutingResult;

public class CsvResultConsumer implements Consumer<PlanRoutingResult> {
	private final BufferedWriter writer;

	public CsvResultConsumer(OutputStream outputStream) throws IOException {
		this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));

		writer.write(String.join(";", new String[] { "plan_id", "trip_id", "number_of_transfers", "is_only_walk",
				"is_ticket_valid", "ticket_price", "in_vehicle_time", "in_vehicle_distance", "transfer_walk_time",
				"transfer_walk_distance", "initial_waiting_time", "transfer_waiting_time", "access_egress_walk_time",
				"access_egress_walk_distance", "frequency", "is_plan_ticket_valid", "plan_ticket_price" }) + "\n");
		writer.flush();
	}

	@Override
	public void accept(PlanRoutingResult planResult) {
		try {
			for (TripRoutingResult result : planResult.getTripResults()) {
				writer.write(String.join(";", new String[] { planResult.getPlanId(), result.getTripId(),
						String.valueOf(result.getNumberOfTransfers()), String.valueOf(result.isOnlyWalk()),
						String.valueOf(result.isTicketPriceValid()), String.valueOf(result.getTicketPrice()),
						String.valueOf(result.getInVehicleTime()), String.valueOf(result.getInVehicleDistance()),
						String.valueOf(result.getTransferWalkTime()), String.valueOf(result.getTransferWalkDistance()),
						String.valueOf(result.getInitialWaitingTime()), String.valueOf(result.getTransferWaitingTime()),
						String.valueOf(result.getAccessEgressWalkTime()),
						String.valueOf(result.getAccessEgressWalkDistance()), String.valueOf(result.getFrequency()),
						String.valueOf(planResult.isTicketPriceValid()), String.valueOf(planResult.getTicketPrice()) })
						+ "\n");
				writer.flush();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
