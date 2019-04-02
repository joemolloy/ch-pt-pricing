package ch.ethz.matsim.ch_pt_utils.routing.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

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
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStageTransformer;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.TicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Authority;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalReader;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Zone;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.Switzerland;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb.data.Triangle;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb.data.TriangleReader;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb.data.TriangleRegistry;
import ch.ethz.matsim.ch_pt_utils.routing.RoutingParameters;
import ch.ethz.matsim.ch_pt_utils.routing.router.DefaultRouterFactory;
import ch.ethz.matsim.ch_pt_utils.routing.router.RouterFactory;

public class RunBatchRouting {
	static public void main(String[] args) throws IOException, ConfigurationException, InterruptedException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("network-path", "schedule-path", "requests-path", "output-path", "zones-path",
						"triangles-path") //
				.allowOptions("threads", "batch-size", "parameters-path") //
				.build();

		File zonesPath = new File(cmd.getOptionStrict("zones-path"));
		File trianglesPath = new File(cmd.getOptionStrict("triangles-path"));

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

		ZonalReader zonalReader = new ZonalReader();
		Collection<Authority> authorities = zonalReader.readAuthorities(zonesPath);
		Collection<Zone> zones = zonalReader.readZones(zonesPath, authorities);

		ZonalRegistry zonalRegistry = new ZonalRegistry(authorities, zones);

		TriangleReader triangleReader = new TriangleReader();
		Collection<Triangle> triangles = triangleReader.read(trianglesPath);

		TriangleRegistry triangleRegistry = new TriangleRegistry(triangles);

		TransitStageTransformer transitStageTransformer = new TransitStageTransformer(scenario.getTransitSchedule());
		TicketGenerator ticketGenerator = Switzerland.createTicketGenerator(zonalRegistry, triangleRegistry);

		RouterFactory factory = new DefaultRouterFactory(parameters, scenario.getNetwork(),
				scenario.getTransitSchedule(), transitStageTransformer, ticketGenerator);
		BatchRouter batchRouter = new BatchRouter(factory, numberOfRunners, batchSize);

		batchRouter.run(new PlanRoutingRequestIterator(requestIterator), resultConsumer, numberOfRequests);

		requestStream.close();
		outputStream.close();
	}
}
