package ch.ethz.matsim.ch_pt_utils.cost.use_cases;

import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.AWelle;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.EngadinMobil;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.Frimobil;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.Libero;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.Ostwind;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.Passepartout;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.TVZG;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.TransReno;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.Unireso;
import ch.ethz.matsim.ch_pt_utils.cost.use_cases.switzerland.ZVV;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.ZonalTicketGenerator;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.data.ZonalRegistry;
import ch.ethz.matsim.ch_pt_utils.cost.zonal.tickets.CompositeZonalTicketGenerator;

public class Switzerland {
	private Switzerland() {
	}

	static public ZonalTicketGenerator createTicketGenerator(ZonalRegistry zonalRegistry) {
		CompositeZonalTicketGenerator zonalTicketGenerator = new CompositeZonalTicketGenerator();
		zonalTicketGenerator.addGenerator(AWelle.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(EngadinMobil.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(Frimobil.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(Libero.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(Ostwind.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(Passepartout.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(TransReno.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(TVZG.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(Unireso.createTicketGenerator(zonalRegistry));
		zonalTicketGenerator.addGenerator(ZVV.createTicketGenerator(zonalRegistry));
		return zonalTicketGenerator;
	}
}
