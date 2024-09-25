package ch.ethz.matsim.ch_pt_utils.server;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.geotools.referencing.CRS;
import org.gnu.glpk.GLPK;
import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;

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
import ch.ethz.matsim.ch_pt_utils.server.routing.RoutingHandler;

import ch.sbb.matsim.routing.pt.raptor.OccupancyData;
import ch.sbb.matsim.routing.pt.raptor.RaptorStaticConfig;
import ch.sbb.matsim.routing.pt.raptor.RaptorUtils;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptor;
import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorData;
import io.javalin.Javalin;

public class RunRoutingServer {
	static public void main(String[] args)
			throws Exception, IOException {

		try {
			System.out.println(CRS.decode("EPSG:2056"));
			System.out.println(GLPK.glp_version());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}


		int port = Integer.parseInt(args[0]);
		File transitSchedulePath = new File(args[1]);
		File networkPath = new File(args[2]);
		File zonesPath = new File(args[3]);
		File trianglesPath = new File(args[4]);

		// Read schedule data
		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);

		// Set up cost calculation

		ZonalReader zonalReader = new ZonalReader();
		Collection<Authority> authorities = zonalReader.readAuthorities(zonesPath);
		Collection<Zone> zones = zonalReader.readZones(zonesPath, authorities);

		ZonalRegistry zonalRegistry = new ZonalRegistry(authorities, zones);

		TriangleReader triangleReader = new TriangleReader();
		Collection<Triangle> triangles = triangleReader.read(trianglesPath);

		TriangleRegistry triangleRegistry = new TriangleRegistry(triangles);
		
		new TransitScheduleReader(scenario).readFile(transitSchedulePath.toString());
		new MatsimNetworkReader(scenario.getNetwork()).readFile(networkPath.toString());

		TicketGenerator ticketGenerator = Switzerland.createTicketGenerator(zonalRegistry, triangleRegistry);
		TransitStageTransformer transformer = new TransitStageTransformer(scenario.getTransitSchedule());

		// Collect travel utilities
		Set<String> transitModes = new HashSet<>();
		for (TransitLine transitLine : scenario.getTransitSchedule().getTransitLines().values()) {
			for (TransitRoute transitRoute : transitLine.getRoutes().values()) {
				transitModes.add(transitRoute.getTransportMode());
			}
		}
			
		OccupancyData occupancyData = new OccupancyData();
		RaptorStaticConfig srrStaticConfig = RaptorUtils.createStaticConfig(config);
	
		SwissRailRaptorData srrData = SwissRailRaptorData.create(scenario.getTransitSchedule(),
				scenario.getTransitVehicles(), srrStaticConfig, scenario.getNetwork(), occupancyData);
		SwissRailRaptor raptor = new SwissRailRaptor.Builder(srrData, config).build();

		Javalin app = Javalin.create();
		app.enableCorsForAllOrigins();
		app.post("/api", new RoutingHandler(raptor, ticketGenerator, scenario.getNetwork(), scenario.getTransitSchedule(), transformer, CRS.decode("EPSG:2056")));
		app.get("/", new FrontendHandler());
		app.start(port);
	}
}
