package ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal;

import java.util.Set;

import ch.ethz.matsim.ch_pt_utils.cost.tickets.zonal.data.Zone;

public interface ZonalTicketCalculator {
	double calculateValidity(Set<Zone> zones);

	double calculateSingleTicketPrice(Set<Zone> zones, boolean halfFare);

	double calculateDayTicketPrice(Set<Zone> zones, boolean halfFare);
}
