package ch.ethz.matsim.ch_pt_utils.routing.batch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.function.Consumer;

import ch.ethz.matsim.ch_pt_utils.routing.RoutingResult;

public class CsvResultConsumer implements Consumer<RoutingResult> {
	private final BufferedWriter writer;

	public CsvResultConsumer(OutputStream outputStream) throws IOException {
		this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));

		writer.write(String.join(";",
				new String[] { "request_id", "number_of_transfers", "is_only_walk", "single_full_fare",
						"single_half_fare", "in_vehicle_time", "in_vehicle_distance", "transfer_walk_time",
						"transfer_walk_distance", "initial_waiting_time", "transfer_waiting_time",
						"access_egress_walk_time", "access_egress_walk_distance", "frequency" })
				+ "\n");
		writer.flush();
	}

	@Override
	public void accept(RoutingResult result) {
		try {
			writer.write(String.join(";", new String[] { result.getRequestId(),
					String.valueOf(result.getNumberOfTransfers()), String.valueOf(result.isOnlyWalk()),
					String.valueOf(result.getSingleFullFare()), String.valueOf(result.getSingleHalfFare()),
					String.valueOf(result.getInVehicleTime()), String.valueOf(result.getInVehicleDistance()),
					String.valueOf(result.getTransferWalkTime()), String.valueOf(result.getTransferWalkDistance()),
					String.valueOf(result.getInitialWaitingTime()), String.valueOf(result.getTransferWaitingTime()),
					String.valueOf(result.getAccessEgressWalkTime()),
					String.valueOf(result.getAccessEgressWalkDistance()), String.valueOf(result.getFrequency()) })
					+ "\n");
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
