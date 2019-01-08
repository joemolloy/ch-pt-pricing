package ch.ethz.matsim.ch_pt_utils.costs.t603;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.matsim.api.core.v01.Coord;
import org.matsim.core.utils.collections.QuadTree;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import ch.ethz.matsim.ch_pt_utils.costs.t603.data.SpatialStation;
import ch.ethz.matsim.ch_pt_utils.costs.t603.data.Station;
import ch.ethz.matsim.ch_pt_utils.costs.t603.raw.RawHafasStation;

public class SpatialT603 {
	private final QuadTree<SpatialStation> stations;

	private SpatialT603(QuadTree<SpatialStation> stations) {
		this.stations = stations;
	}

	public SpatialStation getClosestStation(Coord coord) {
		return stations.getClosest(coord.getX(), coord.getY());
	}

	static public SpatialT603 read(File hafasPath, T603 t603) throws IOException, NoSuchAuthorityCodeException,
			FactoryException, MismatchedDimensionException, TransformException {
		File stationFile = new File(hafasPath, "BFKOORD_GEO");

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stationFile)));
		String line = null;

		Map<String, RawHafasStation> rawStations = new HashMap<>();

		while ((line = reader.readLine()) != null) {
			double longitude = Double.parseDouble(line.substring(10, 20).trim());
			double latitude = Double.parseDouble(line.substring(20, 30).trim());
			String name = line.split("%")[1].trim();
			rawStations.put(name, new RawHafasStation(name, longitude, latitude));
		}

		reader.close();

		List<SpatialStation> spatialStations = new LinkedList<>();
		CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem lv95 = CRS.decode("EPSG:2056");
		MathTransform transform = CRS.findMathTransform(wgs84, lv95, false);

		for (Station station : t603.getStations().values()) {
			String hafasName = station.getName();

			hafasName = hafasName.replace("-Frontière", "");
			hafasName = hafasName.replace("Abzweigung", "Abzw.");
			hafasName = hafasName.replace(", Zivilschutzanlage", ",Zivilschutzanlage");
			hafasName = hafasName.replace("Wohlen", "Wohlen AG");
			hafasName = hafasName.replace("Schwanden", "Schwanden GL");
			hafasName = hafasName.replace("Hüntwangen-Wil SG", "Hüntwangen-Wil");
			hafasName = hafasName.replace("Crémimes", "Crémines");
			hafasName = hafasName.replace("Oberkirch", "Oberkirch LU");
			hafasName = hafasName.replace("Ambri-Piotta", "Ambrì-Piotta");
			hafasName = hafasName.replace("S.Nazzaro", "S. Nazzaro");
			hafasName = hafasName.replace("Gaggiolo Confine", "Gaggiolo, Confine (I)");
			hafasName = hafasName.replace("Raron, Turtig,", "Raron, Turtig");
			hafasName = hafasName.replace("Iselle transito", "Iselle, Stazione");
			hafasName = hafasName.replace("Pino transito", "Pino-Tronzano");
			hafasName = hafasName.replace("Root D 4", "Root D4");
			hafasName = hafasName.replace("Benken", "Benken SG");

			if (hafasName.equals("Wil")) {
				hafasName = "Wil SG";
			}

			hafasName = hafasName.trim();

			if (!rawStations.containsKey(hafasName)) {
				throw new IllegalStateException("Cannot find HAFAS station '" + hafasName + "'");
			} else {
				RawHafasStation hafasStation = rawStations.get(hafasName);

				DirectPosition latLon = new DirectPosition2D(hafasStation.latitude, hafasStation.longitude);
				DirectPosition result = new DirectPosition2D();
				transform.transform(latLon, result);

				spatialStations.add(
						new SpatialStation(station, new Coord(result.getCoordinate()[0], result.getCoordinate()[1])));
			}
		}

		double minX = spatialStations.stream().mapToDouble(s -> s.coord.getX()).min().getAsDouble();
		double maxX = spatialStations.stream().mapToDouble(s -> s.coord.getX()).max().getAsDouble();
		double minY = spatialStations.stream().mapToDouble(s -> s.coord.getY()).min().getAsDouble();
		double maxY = spatialStations.stream().mapToDouble(s -> s.coord.getY()).max().getAsDouble();

		QuadTree<SpatialStation> tree = new QuadTree<>(minX, minY, maxX, maxY);
		spatialStations.forEach(s -> tree.put(s.coord.getX(), s.coord.getY(), s));

		return new SpatialT603(tree);
	}
}
