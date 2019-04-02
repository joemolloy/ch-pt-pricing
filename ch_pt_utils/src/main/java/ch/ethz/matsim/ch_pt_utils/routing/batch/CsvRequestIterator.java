package ch.ethz.matsim.ch_pt_utils.routing.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.matsim.api.core.v01.Coord;

import ch.ethz.matsim.ch_pt_utils.routing.TripRoutingRequest;

public class CsvRequestIterator implements Iterator<TripRoutingRequest> {
	private final BufferedReader reader;

	private List<String> header = null;
	private List<String> next = null;

	static public int getNumberOfRequests(InputStream inputStream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		int numberOfRequests = -1;

		while (reader.readLine() != null) {
			numberOfRequests++;
		}

		return numberOfRequests;
	}

	public CsvRequestIterator(InputStream inputStream) throws IOException {
		this.reader = new BufferedReader(new InputStreamReader(inputStream));

		advance();

		if (next != null) {
			header = new ArrayList<>(next);
			advance();
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	private void advance() {
		try {
			String row = reader.readLine();

			if (row == null) {
				next = null;
			} else {
				next = Arrays.asList(row.split(";"));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public TripRoutingRequest next() {
		String planId = next.get(header.indexOf("plan_id"));
		String tripId = next.get(header.indexOf("trip_id"));
		double originX = Double.parseDouble(next.get(header.indexOf("origin_x")));
		double originY = Double.parseDouble(next.get(header.indexOf("origin_y")));
		double destinationX = Double.parseDouble(next.get(header.indexOf("destination_x")));
		double destinationY = Double.parseDouble(next.get(header.indexOf("destination_y")));
		double departureTime = Double.parseDouble(next.get(header.indexOf("departure_time")));

		Coord originCoord = new Coord(originX, originY);
		Coord destinationCoord = new Coord(destinationX, destinationY);

		TripRoutingRequest returnRequest = new TripRoutingRequest(planId, tripId, originCoord, destinationCoord,
				departureTime);

		advance();
		return returnRequest;
	}
}
