package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class DistanceTariff {
	private final static double[] LOOKUP = createLookupTable();

	public static double calculateCost(double distance) {
		if (distance <= 1500.0) {
			int intDistance = (int) distance;
			return LOOKUP[intDistance];
		} else {
			throw new IllegalStateException("Distance is too big: " + distance);
		}
	}

	public static void write(File path) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
		writer.write(String.format("distance;price\n"));

		for (int i = 0; i < LOOKUP.length; i++) {
			writer.write(String.format("%d;%.2f\n", i, LOOKUP[i]));
		}

		writer.close();
	}

	static public void main(String[] args) throws IOException {
		write(new File(args[0]));
	}

	private static double[] createLookupTable() {
		double[] costTable = new double[1500];

		for (int distanceIndex = 0; distanceIndex < 1500; distanceIndex++) {
			// First, calculation is based on distance classes (e.g. for distances <30km,
			// the distance is accounted for every 2nd kilometer).

			int distance = distanceIndex;

			if (distance <= 8) {
				distance = (int) (Math.ceil((double) distance / 4.0) * 4.0);
			} else if (distance <= 30) {
				distance = (int) (Math.ceil((double) distance / 2.0) * 2.0);
			} else if (distance <= 100) {
				distance = (int) (Math.ceil((double) distance / 3.0) * 3.0);
			} else if (distance <= 150) {
				distance = (int) (Math.ceil((double) distance / 5.0) * 5.0);
			} else if (distance <= 300) {
				distance = (int) (Math.ceil((double) distance / 10.0) * 10.0);
			} else {
				distance = (int) (Math.ceil((double) distance / 20.0) * 20.0);
			}

			// Second, prices per kilometer are different for distance classes. Each
			// kilometer has therefore its own "value".

			double cost = 0.0;

			for (int currentDistance = 1; currentDistance < distance + 1; currentDistance++) {
				if (currentDistance <= 4) {
					cost += 44.51;
				} else if (currentDistance <= 14) {
					cost += 42.30;
				} else if (currentDistance <= 48) {
					cost += 37.24;
				} else if (currentDistance <= 150) {
					cost += 26.46;
				} else if (currentDistance <= 200) {
					cost += 25.71;
				} else if (currentDistance <= 250) {
					cost += 22.85;
				} else if (currentDistance <= 300) {
					cost += 20.63;
				} else if (currentDistance <= 480) {
					cost += 20.09;
				} else {
					cost += 19.85;
				}
			}

			// Prices for distances < 70km are rounded to 0.2 CHF, prices above to 1 CHF

			if (distance <= 69) {
				cost = Math.ceil(cost / 20.0) * 20.0;
			} else {
				cost = Math.ceil(cost / 100.0) * 100.0;
			}

			// The minimum price is 3 CHF

			cost = Math.max(cost, 300.0);
			costTable[distanceIndex] = cost / 100.0;
		}

		return costTable;
	}
}
