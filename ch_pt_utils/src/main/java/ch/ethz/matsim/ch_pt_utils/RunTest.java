package ch.ethz.matsim.ch_pt_utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ch.ethz.matsim.ch_pt_utils.costs.CostCalculator;
import ch.ethz.matsim.ch_pt_utils.costs.t603.SpatialT603;
import ch.ethz.matsim.ch_pt_utils.costs.t603.T603;
import ch.ethz.matsim.ch_pt_utils.costs.t603.data.Station;
import ch.ethz.matsim.ch_pt_utils.costs.zonal.ZonalSystem;

public class RunTest {
	static public void main(String[] args) throws JsonParseException, JsonMappingException, IOException,
			MismatchedDimensionException, NoSuchAuthorityCodeException, FactoryException, TransformException {
		T603 t603 = T603.read(new File("/home/sebastian/ch_pt_utils/data/temp/triangles.json"));

		Station originStation = t603.getStationByName("Zürich HB");
		Station destinationStation = t603.getStationByName("Genève");

		double distance = t603.computeDistance(originStation, destinationStation);
		double price = t603.computePrice(distance, false);

		System.out.println("From: " + originStation);
		System.out.println("To: " + destinationStation);
		System.out.println("Distance: " + distance);
		System.out.println("Price: " + price);

		File hafasPath = new File("/run/media/sebastian/shoerl_data/scenarios/switzerland/data/hafas");
		SpatialT603 spatialT603 = SpatialT603.read(hafasPath, t603);

		File zonesFile = new File(
				"/run/media/sebastian/shoerl_data/tickets/Tarifzonen NPVM LV95_area/Tarifzonen NPVM LV95_area.SHP");
		ZonalSystem zonalPricing = ZonalSystem.read(zonesFile);

		CostCalculator costCalculator = new CostCalculator(t603, spatialT603, zonalPricing);

		CoordinateReferenceSystem wgs84 = CRS.decode("EPSG:4326");
		CoordinateReferenceSystem lv95 = CRS.decode("EPSG:2056");
		MathTransform transform = CRS.findMathTransform(wgs84, lv95, false);

		List<Coord> lonlats = Arrays.asList(
				new Coord(47.419850, 8.501420), //
				new Coord(47.378481, 8.538424), //
				new Coord(46.948612, 7.436521) //
				);

		List<Coord> coords = new LinkedList<>();

		for (Coord lonlat : lonlats) {
			DirectPosition latLon = new DirectPosition2D(lonlat.getX(), lonlat.getY());
			DirectPosition result = new DirectPosition2D();
			transform.transform(latLon, result);
			coords.add(new Coord(result.getCoordinate()[0], result.getCoordinate()[1]));
		}

		System.out.println("Price: " + costCalculator.computeCost(coords, false));
	}
}
