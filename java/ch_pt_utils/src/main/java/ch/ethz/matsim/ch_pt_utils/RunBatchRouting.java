package ch.ethz.matsim.ch_pt_utils;

import ch.ethz.matsim.baseline_scenario.config.CommandLine;
import ch.ethz.matsim.baseline_scenario.config.CommandLine.ConfigurationException;

public class RunBatchRouting {
	static public void main(String[] args) throws ConfigurationException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("input-path", "output-path") //
				.build();
	}
}
