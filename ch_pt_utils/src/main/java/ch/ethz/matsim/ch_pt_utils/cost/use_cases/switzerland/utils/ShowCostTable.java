package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.utils;

import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb.DistanceTariff;

public class ShowCostTable {
	static public void main(String[] args) {
		double distance = 0.0;

		while (distance < 1500.0) {
			System.out.println(String.format("Distance: %.0fkm, Price: %.2f CHF", distance,
					DistanceTariff.calculateCost(distance)));
			distance += 1.0;
		}
	}
}
