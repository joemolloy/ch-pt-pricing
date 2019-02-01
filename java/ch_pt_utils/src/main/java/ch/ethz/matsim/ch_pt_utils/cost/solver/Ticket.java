package ch.ethz.matsim.ch_pt_utils.cost.solver;

import java.util.BitSet;

public class Ticket {
	private final String description;
	private final BitSet coverage;
	private final double price;
	private final int numberOfStages;

	public Ticket(int numberOfStages, double price, String description) {
		this.coverage = new BitSet(numberOfStages);
		this.description = description;
		this.price = price;
		this.numberOfStages = numberOfStages;
	}

	public Ticket(int numberOfStages, double price) {
		this(numberOfStages, price, null);
	}

	public BitSet getCoverage() {
		return coverage;
	}
	
	public double getPrice() {
		return price;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		String description = this.description == null ? "No description" : this.description;
		return String.format("Ticket(%s, Price %.2f %s)", description, price, printCoverage());
	}

	private String printCoverage() {
		String coverage = "";

		for (int i = 0; i < numberOfStages; i++) {
			if (this.coverage.get(i)) {
				coverage += "X";
			} else {
				coverage += " ";
			}
		}

		return "[" + coverage + "]";
	}
}
