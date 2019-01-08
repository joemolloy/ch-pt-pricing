package ch.ethz.matsim.ch_pt_utils;

import java.io.File;
import java.io.IOException;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.operation.TransformException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ch.ethz.matsim.ch_pt_utils.costs.t603.SpatialT603;
import ch.ethz.matsim.ch_pt_utils.costs.t603.T603;
import ch.ethz.matsim.ch_pt_utils.costs.t603.data.Station;

public class RunTest {
	static public void main(String[] args) throws JsonParseException, JsonMappingException, IOException,
			MismatchedDimensionException, NoSuchAuthorityCodeException, FactoryException, TransformException {
		T603 t603 = T603.read(new File("/home/sebastian/ch_pt_utils/data/temp/triangles.json"));

		Station originStation = t603.getStationByName("Zürich HB");
		Station destinationStation = t603.getStationByName("Genève");

		double distance = t603.computeDistance(originStation, destinationStation);
		double price = t603.computePrice(distance);

		System.out.println("From: " + originStation);
		System.out.println("To: " + destinationStation);
		System.out.println("Distance: " + distance);
		System.out.println("Price: " + price);

		File hafasPath = new File("/run/media/sebastian/shoerl_data/scenarios/switzerland/data/hafas");
		SpatialT603 spatial = SpatialT603.read(hafasPath, t603);
	}
}
