package ch.ethz.matsim.ch_pt_utils.routing.batch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.matsim.api.core.v01.Coord;

import ch.ethz.matsim.ch_pt_utils.routing.RoutingRequest;

public class CsvRequestIterator implements Iterator<RoutingRequest> {
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

		String row = this.reader.readLine();

		if (row == null) {
			throw new IllegalStateException();
		} else {
			header = Arrays.asList(row.split(";"));
		}
	}

	@Override
	public boolean hasNext() {
		try {
			String row = reader.readLine();

			if (row == null) {
				return false;
			} else {
				next = Arrays.asList(row.split(";"));
				return true;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public RoutingRequest next() {
		String requestId = next.get(header.indexOf("request_id"));
		double originX = Double.parseDouble(next.get(header.indexOf("origin_x")));
		double originY = Double.parseDouble(next.get(header.indexOf("origin_y")));
		double destinationX = Double.parseDouble(next.get(header.indexOf("destination_x")));
		double destinationY = Double.parseDouble(next.get(header.indexOf("destination_y")));
		double departureTime = Double.parseDouble(next.get(header.indexOf("departure_time")));

		Coord originCoord = new Coord(originX, originY);
		Coord destinationCoord = new Coord(destinationX, destinationY);

		return new RoutingRequest(requestId, originCoord, destinationCoord, departureTime);
	}
}
