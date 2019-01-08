package ch.ethz.matsim.ch_pt_utils.costs.run;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.matsim.api.core.v01.Coord;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import ch.ethz.matsim.ch_pt_utils.costs.CostCalculator;
import ch.ethz.matsim.ch_pt_utils.costs.t603.SpatialT603;
import ch.ethz.matsim.ch_pt_utils.costs.t603.T603;
import ch.ethz.matsim.ch_pt_utils.costs.zonal.ZonalSystem;

public class ComputeMZCosts {
	static public void main(String[] args) throws IOException, MismatchedDimensionException, TransformException,
			NoSuchAuthorityCodeException, FactoryException {
		String inputPath = args[0];
		String outputPath = args[1];

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));

		List<String> header = null;
		String line;

		CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem lv95 = CRS.decode("EPSG:2056");
		MathTransform transform = CRS.findMathTransform(wgs84, lv95, false);

		T603 t603 = T603.read(new File("/home/sebastian/ch_pt_utils/data/temp/triangles.json"));
		File hafasPath = new File("/run/media/sebastian/shoerl_data/scenarios/switzerland/data/hafas");
		SpatialT603 spatialT603 = SpatialT603.read(hafasPath, t603);
		File zonesFile = new File(
				"/run/media/sebastian/shoerl_data/tickets/Tarifzonen NPVM LV95_area/Tarifzonen NPVM LV95_area.SHP");
		ZonalSystem zonalPricing = ZonalSystem.read(zonesFile);

		CostCalculator costCalculator = new CostCalculator(t603, spatialT603, zonalPricing);

		writer.write(
				"HHNR;WEGNR;price_full_pt.1;price_full_pt.2;price_full_pt.3;price_full_pt.4;price_half_pt.1;price_half_pt.2;price_half_pt.3;price_half_pt.4\n");

		while ((line = reader.readLine()) != null) {
			List<String> row = Arrays.asList(line.split(";"));

			if (header == null) {
				header = row;
			} else {
				List<Double> fullPrices = new LinkedList<>();
				List<Double> halfPrices = new LinkedList<>();

				for (int i = 1; i < 5; i++) {
					int transfers = (int) Double.parseDouble(row.get(header.indexOf("transfers_" + i)));

					if (transfers != -99) {
						List<Double> x = Arrays.asList(row.get(header.indexOf("x_" + i)).split(",")).stream()
								.map(Double::parseDouble).collect(Collectors.toList());
						List<Double> y = Arrays.asList(row.get(header.indexOf("y_" + i)).split(",")).stream()
								.map(Double::parseDouble).collect(Collectors.toList());

						List<Coord> coords = new LinkedList<>();

						for (int j = 0; j < transfers + 2; j++) {
							DirectPosition latLon = new DirectPosition2D(x.get(j), y.get(j));
							DirectPosition result = new DirectPosition2D();
							transform.transform(latLon, result);
							coords.add(new Coord(result.getCoordinate()[0], result.getCoordinate()[1]));
						}

						fullPrices.add(costCalculator.computeCost(coords, false));
						halfPrices.add(costCalculator.computeCost(coords, true));
					} else {
						fullPrices.add(Double.NaN);
						halfPrices.add(Double.NaN);
					}
				}

				String HHNR = row.get(header.indexOf("HHNR"));
				String WEGNR = row.get(header.indexOf("WEGNR"));

				writer.write(String.join(";", new String[] { //
						HHNR, WEGNR, //
						String.valueOf(fullPrices.get(0)), String.valueOf(fullPrices.get(1)), //
						String.valueOf(fullPrices.get(2)), String.valueOf(fullPrices.get(3)), //
						String.valueOf(halfPrices.get(0)), String.valueOf(halfPrices.get(1)), //
						String.valueOf(halfPrices.get(2)), String.valueOf(halfPrices.get(3)) //
				}) + "\n");
				writer.flush();
			}
		}

		reader.close();
		writer.close();
	}
}
