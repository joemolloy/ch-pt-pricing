package ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.CompositeTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.TicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.TrajectoryTicketAdapter;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.TrajectoryTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.filter.ModeTrajectoryFilter;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.trajectory.filter.TrajectoryStageFilter;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.ZonalTicketAdapter;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.tickets.CompositeZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb.SBBTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.sbb.data.TriangleRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.AWelle;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.EngadinMobil;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.Frimobil;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.Libero;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.Mobilis;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.Ostwind;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.Passepartout;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.TVSZ;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.TVZG;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.TransReno;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.Unireso;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.zonal.ZVV;

public class Switzerland {
	private Switzerland() {
	}

	static public TicketGenerator createTrajectoryTicketGenerator(ZonalRegistry zonalRegistry,
			TriangleRegistry triangleRegistry) {
		TrajectoryStageFilter filter = new ModeTrajectoryFilter("rail");
		TrajectoryTicketGenerator generator = new SBBTicketGenerator(triangleRegistry, zonalRegistry);

		return new TrajectoryTicketAdapter(filter, generator);
	}

	static public TicketGenerator createZonalTicketGenerator(ZonalRegistry zonalRegistry) {
		CompositeZonalTicketGenerator zonalTicketGenerator = new CompositeZonalTicketGenerator();
		zonalTicketGenerator.addGenerator(AWelle.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(EngadinMobil.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(Frimobil.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(Libero.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(Mobilis.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(Ostwind.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(Passepartout.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(TransReno.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(TVSZ.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(TVZG.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(Unireso.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(ZVV.createTicketGenerator(zonalRegistry));

		return new ZonalTicketAdapter(zonalTicketGenerator, zonalRegistry);
	}

	static public TicketGenerator createTicketGenerator(ZonalRegistry zonalRegistry,
			TriangleRegistry triangleRegistry) {
		CompositeTicketGenerator ticketGenerator = new CompositeTicketGenerator();
		ticketGenerator.addGenerator(createTrajectoryTicketGenerator(zonalRegistry, triangleRegistry));
		ticketGenerator.addGenerator(createZonalTicketGenerator(zonalRegistry));
		ticketGenerator.addGenerator(TransReno.createTrajectoryTicketGenerator(zonalRegistry));

		DistanceTicketGenerator fallbackTicketGenerator = new DistanceTicketGenerator(ticketGenerator);

		return fallbackTicketGenerator;
	}
}
