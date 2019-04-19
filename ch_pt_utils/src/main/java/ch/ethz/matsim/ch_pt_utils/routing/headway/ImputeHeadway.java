package ch.ethz.matsim.ch_pt_utils.routing.headway;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.MatsimNetworkReader;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.population.io.PopulationWriter;
import org.matsim.core.router.StageActivityTypes;
import org.matsim.core.router.StageActivityTypesImpl;
import org.matsim.core.router.TripStructureUtils;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.pt.PtConstants;
import org.matsim.pt.transitSchedule.api.TransitScheduleReader;

import ch.ethz.matsim.baseline_scenario.config.CommandLine;
import ch.ethz.matsim.baseline_scenario.config.CommandLine.ConfigurationException;
import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRoute;
import ch.ethz.matsim.baseline_scenario.transit.routing.DefaultEnrichedTransitRouteFactory;
import ch.ethz.matsim.ch_pt_utils.routing.PlanRoutingRequest;
import ch.ethz.matsim.ch_pt_utils.routing.PlanRoutingResult;
import ch.ethz.matsim.ch_pt_utils.routing.RoutingParameters;
import ch.ethz.matsim.ch_pt_utils.routing.TripRoutingRequest;
import ch.ethz.matsim.ch_pt_utils.routing.batch.BatchRouter;
import ch.ethz.matsim.ch_pt_utils.routing.router.DefaultRouterFactory;
import ch.ethz.matsim.ch_pt_utils.routing.router.RouterFactory;

public class ImputeHeadway {
	static public void main(String[] args) throws ConfigurationException, InterruptedException {
		CommandLine cmd = new CommandLine.Builder(args) //
				.requireOptions("network-path", "schedule-path", "population-path", "output-path") //
				.build();

		Config config = ConfigUtils.createConfig();
		Scenario scenario = ScenarioUtils.createScenario(config);
		scenario.getPopulation().getFactory().getRouteFactories().setRouteFactory(DefaultEnrichedTransitRoute.class,
				new DefaultEnrichedTransitRouteFactory());

		new MatsimNetworkReader(scenario.getNetwork()).readFile(cmd.getOptionStrict("network-path"));
		new TransitScheduleReader(scenario).readFile(cmd.getOptionStrict("schedule-path"));
		new PopulationReader(scenario).readFile(cmd.getOptionStrict("population-path"));

		RoutingParameters parameters = new RoutingParameters();

		RouterFactory factory = new DefaultRouterFactory(parameters, scenario.getNetwork(),
				scenario.getTransitSchedule());

		StageActivityTypes stageActivityTypes = new StageActivityTypesImpl(PtConstants.TRANSIT_ACTIVITY_TYPE);
		PopulationIterator populationIterator = new PopulationIterator(
				scenario.getPopulation().getPersons().values().iterator(), stageActivityTypes);

		PopulationConsumer populationConsumer = new PopulationConsumer(scenario.getPopulation(), stageActivityTypes);

		BatchRouter batchRouter = new BatchRouter(factory, Runtime.getRuntime().availableProcessors(), 100);
		batchRouter.run(populationIterator, populationConsumer);

		new PopulationWriter(scenario.getPopulation()).write(cmd.getOptionStrict("output-path"));
	}

	static private class PopulationIterator implements Iterator<PlanRoutingRequest> {
		private final Iterator<? extends Person> personIterator;
		private final StageActivityTypes stageActivityTypes;

		public PopulationIterator(Iterator<? extends Person> personIterator, StageActivityTypes stageActivityTypes) {
			this.personIterator = personIterator;
			this.stageActivityTypes = stageActivityTypes;
		}

		@Override
		public boolean hasNext() {
			return personIterator.hasNext();
		}

		@Override
		public PlanRoutingRequest next() {
			Person person = personIterator.next();
			List<TripRoutingRequest> tripRequests = new LinkedList<>();

			int tripIndex = 0;

			for (TripStructureUtils.Trip trip : TripStructureUtils.getTrips(person.getSelectedPlan(),
					stageActivityTypes)) {
				Coord originCoord = trip.getOriginActivity().getCoord();
				Coord destinationCoord = trip.getDestinationActivity().getCoord();
				double departureTime = trip.getOriginActivity().getEndTime();

				tripRequests.add(new TripRoutingRequest(person.getId().toString(), String.valueOf(tripIndex),
						originCoord, destinationCoord, departureTime));
				tripIndex++;
			}

			return new PlanRoutingRequest(person.getId().toString(), tripRequests);
		}
	}

	static private class PopulationConsumer implements Consumer<PlanRoutingResult> {
		private final Population population;
		private final StageActivityTypes stageActivityTypes;

		public PopulationConsumer(Population population, StageActivityTypes stageActivityTypes) {
			this.population = population;
			this.stageActivityTypes = stageActivityTypes;
		}

		@Override
		public void accept(PlanRoutingResult result) {
			Id<Person> personId = Id.createPersonId(result.getPlanId());
			Person person = population.getPersons().get(personId);

			int tripIndex = 0;

			for (TripStructureUtils.Trip trip : TripStructureUtils.getTrips(person.getSelectedPlan(),
					stageActivityTypes)) {
				double frequency = result.getTripResults().get(tripIndex).getFrequency();
				double headway_min = frequency > 0.0 ? (1.0 / frequency) / 60.0 : 120.0;

				trip.getOriginActivity().getAttributes().putAttribute("headway", headway_min);
				tripIndex++;
			}
		}
	}
}
