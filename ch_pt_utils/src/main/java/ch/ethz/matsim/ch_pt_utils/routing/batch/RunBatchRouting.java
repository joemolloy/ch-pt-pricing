package ch.ethz.matsim.ch_pt_utils.routing.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ethz.matsim.baseline_scenario.config.CommandLine;
import ch.ethz.matsim.baseline_scenario.config.CommandLine.ConfigurationException;
import ch.ethz.matsim.ch_pt_utils.ScheduleUtils;
import ch.ethz.matsim.ch_pt_utils.routing.RoutingParameters;
import ch.ethz.matsim.ch_pt_utils.routing.router.DefaultRouterFactory;
import ch.ethz.matsim.ch_pt_utils.routing.router.RouterFactory;

public class RunBatchRouting {
	static public void main(String[] args) throws IOException, ConfigurationException, InterruptedException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("network-path", "schedule-path", "requests-path", "output-path") //
				.allowOptions("threads", "batch-size", "parameters-path") //
				.build();

		int numberOfRunners = cmd.getOption("threads").map(Integer::parseInt)
				.orElse(Runtime.getRuntime().availableProcessors());

		int batchSize = cmd.getOption("batch-size").map(Integer::parseInt).orElse(100);

		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);

		new MatsimNetworkReader(scenario.getNetwork()).readFile(cmd.getOptionStrict("network-path"));
		new TransitScheduleReader(scenario).readFile(cmd.getOptionStrict("schedule-path"));

		RoutingParameters parameters = new RoutingParameters();

		if (cmd.hasOption("parameters-path")) {
			parameters = new ObjectMapper().readValue(new File(cmd.getOptionStrict("parameters-path")),
					RoutingParameters.class);
		}

		ScheduleUtils.wrapSchedule(scenario.getTransitSchedule(), parameters.scheduleWrappingEndTime);

		InputStream requestCountStream = new FileInputStream(new File(cmd.getOptionStrict("requests-path")));
		int numberOfRequests = CsvRequestIterator.getNumberOfRequests(requestCountStream);
		requestCountStream.close();

		InputStream requestStream = new FileInputStream(new File(cmd.getOptionStrict("requests-path")));
		OutputStream outputStream = new FileOutputStream(new File(cmd.getOptionStrict("output-path")));

		CsvRequestIterator requestIterator = new CsvRequestIterator(requestStream);
		CsvResultConsumer resultConsumer = new CsvResultConsumer(outputStream);

		RouterFactory factory = new DefaultRouterFactory(parameters, scenario.getNetwork(),
				scenario.getTransitSchedule());
		BatchRouter batchRouter = new BatchRouter(factory, numberOfRunners, batchSize);

		batchRouter.run(requestIterator, resultConsumer, numberOfRequests);

		requestStream.close();
		outputStream.close();
	}
}
