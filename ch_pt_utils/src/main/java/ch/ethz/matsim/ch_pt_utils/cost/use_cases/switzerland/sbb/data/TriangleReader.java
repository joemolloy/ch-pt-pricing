package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TriangleReader {
	public Collection<Triangle> read(File path) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		String line = null;
		List<String> header = null;

		Map<Long, Triangle.Builder> builders = new HashMap<>();

		while ((line = reader.readLine()) != null) {
			List<String> row = Arrays.asList(line.split(";"));

			if (header == null) {
				header = row;
			} else {
				long triangleId = Long.parseLong(row.get(header.indexOf("triangle_id")));
				long originHafasId = Long.parseLong(row.get(header.indexOf("origin_hafas_id")));
				long destinationHafasId = Long.parseLong(row.get(header.indexOf("destination_hafas_id")));
				double distance = Double.parseDouble(row.get(header.indexOf("distance")));

				if (!builders.containsKey(triangleId)) {
					builders.put(triangleId, new Triangle.Builder(triangleId));
				}

				Triangle.Builder builder = builders.get(triangleId);
				builder.addDistance(originHafasId, destinationHafasId, distance);
			}
		}

		reader.close();

		List<Triangle> triangles = new LinkedList<>();

		for (Triangle.Builder builder : builders.values()) {
			triangles.add(builder.build());
		}

		return triangles;
	}
}
