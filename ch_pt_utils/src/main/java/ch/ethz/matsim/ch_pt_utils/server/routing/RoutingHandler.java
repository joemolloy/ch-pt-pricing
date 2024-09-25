package ch.ethz.matsim.ch_pt_utils.server.routing;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.matsim.api.core.v01.Coord;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.router.LinkWrapperFacility;
import org.matsim.facilities.Facility;
import org.matsim.pt.transitSchedule.api.TransitLine;
import org.matsim.pt.transitSchedule.api.TransitRoute;
import org.matsim.pt.router.TransitRouter;
import org.matsim.pt.routes.TransitPassengerRoute;
import org.matsim.pt.transitSchedule.api.TransitSchedule;
import org.matsim.pt.transitSchedule.api.TransitStopFacility;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

import ch.ethz.matsim.ch_pt_utils.cost.solver.Ticket;
import ch.ethz.matsim.ch_pt_utils.cost.solver.TicketSolver;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStage;
import ch.ethz.matsim.ch_pt_utils.cost.stages.TransitStageTransformer;
import ch.ethz.matsim.ch_pt_utils.cost.tickets.TicketGenerator;
import ch.ethz.matsim.ch_pt_utils.server.routing.request.PlanRequest;
import ch.ethz.matsim.ch_pt_utils.server.routing.request.TripRequest;
import ch.ethz.matsim.ch_pt_utils.server.routing.response.CoordinateResponse;
import ch.ethz.matsim.ch_pt_utils.server.routing.response.PlanResponse;
import ch.ethz.matsim.ch_pt_utils.server.routing.response.TicketResponse;
import ch.ethz.matsim.ch_pt_utils.server.routing.response.TransitStageResponse;
import ch.ethz.matsim.ch_pt_utils.server.routing.response.TripResponse;
import ch.ethz.matsim.ch_pt_utils.server.routing.response.WalkStageResponse;
import io.javalin.Context;
import io.javalin.Handler;

public class RoutingHandler implements Handler {
	private final MathTransform transform;
	private final MathTransform backTransform;
	private final TransitRouter transitRouter;
	private final TicketGenerator ticketGenerator;
	private final TransitStageTransformer transformer;
	private Network network;
	private TransitSchedule schedule;

	public RoutingHandler(TransitRouter transitRouter,
						  TicketGenerator ticketGenerator, Network network, TransitSchedule scheudle,
						  TransitStageTransformer transformer, CoordinateReferenceSystem scheduleCRS)
			throws NoSuchAuthorityCodeException, FactoryException, NoninvertibleTransformException {

		this.network = network;
		this.schedule = scheudle;
		this.transform = CRS.findMathTransform(CRS.decode("EPSG:4326"), scheduleCRS);
		this.transitRouter = transitRouter;
		this.backTransform = transform.inverse();
		this.ticketGenerator = ticketGenerator;
		this.transformer = transformer;
	}

	@Override
	public synchronized void handle(Context ctx) throws Exception {
		try {
			PlanRequest planRequest = ctx.bodyAsClass(PlanRequest.class);
			PlanResponse planResponse = new PlanResponse();

			boolean isHalfFare = planRequest.isHalfFare;
			planResponse.isHalfFare = isHalfFare;

			List<TransitStage> transitStages = new LinkedList<>();

			for (TripRequest tripRequest : planRequest.trips) {
				DirectPosition originWGS84 = new DirectPosition2D(tripRequest.originLatitude,
						tripRequest.originLongitude);
				DirectPosition destinationWGS84 = new DirectPosition2D(tripRequest.destinationLatitude,
						tripRequest.destinationLongitude);

				DirectPosition originCoordinate = new DirectPosition2D();
				DirectPosition destinationCoordinate = new DirectPosition2D();

				transform.transform(originWGS84, originCoordinate);
				transform.transform(destinationWGS84, destinationCoordinate);

				Coord originCoord = new Coord(originCoordinate.getCoordinate()[0], originCoordinate.getCoordinate()[1]);
				Coord destinationCoord = new Coord(destinationCoordinate.getCoordinate()[0],
						destinationCoordinate.getCoordinate()[1]);

				Link originLink = NetworkUtils.getNearestLink(network, originCoord);
				Link destinationLink = NetworkUtils.getNearestLink(network, destinationCoord);

				Facility originFacility = new LinkWrapperFacility(originLink);
				Facility destinationFacility = new LinkWrapperFacility(destinationLink);

				List<Leg> legs = transitRouter.calcRoute(originFacility, destinationFacility, tripRequest.departureTime, null);

				TripResponse tripResponse = createTripResponse(legs, originLink, destinationLink, 0);
				planResponse.trips.add(tripResponse);

				transitStages.addAll(transformer.getStages(legs));
			}

			if (transitStages.size() > 0) {
				Collection<Ticket> tickets = ticketGenerator.createTickets(transitStages, isHalfFare);
				TicketSolver.Result result = new TicketSolver().solve(transitStages.size(), tickets);

				for (Ticket ticket : result.tickets) {
					TicketResponse ticketResponse = new TicketResponse();

					ticketResponse.description = ticket.getDescription();
					ticketResponse.price = ticket.getPrice();
					planResponse.totalPrice += ticketResponse.price;

					for (int i = 0; i < transitStages.size(); i++) {
						ticketResponse.coverage.add(ticket.getCoverage().get(i));
					}

					planResponse.tickets.add(ticketResponse);
				}
			}

			ctx.json(planResponse);
		} catch (Exception e) {
			PlanResponse planResponse = new PlanResponse();

			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));

			planResponse.error = writer.toString();

			ctx.json(planResponse);
			e.printStackTrace();
		}
	}

	private TripResponse createTripResponse(Collection<Leg> legs, Link originLink, Link destinationLink, double frequency)
			throws MismatchedDimensionException, TransformException {
		TripResponse tripResponse = new TripResponse();

		tripResponse.originStreetName = (String) originLink.getAttributes().getAttribute("osm:way:name");
		tripResponse.destinationStreetName = (String) destinationLink.getAttributes().getAttribute("osm:way:name");
		tripResponse.frequency = frequency;

		if (tripResponse.originStreetName == null) {
			tripResponse.originStreetName = "Unknown";
		}

		if (tripResponse.destinationStreetName == null) {
			tripResponse.destinationStreetName = "Unknown";
		}

		for (Leg leg : legs) {
			System.out.println(leg);
			System.out.println(leg.getMode());
			if (leg.getMode().equals(TransportMode.pt)) {
				TransitStageResponse stageResponse = new TransitStageResponse();
				TransitPassengerRoute route = (TransitPassengerRoute) leg.getRoute();

				stageResponse.departureTime = leg.getDepartureTime().seconds();
				stageResponse.arrivalTime = leg.getDepartureTime().seconds() + leg.getTravelTime().seconds();

				TransitLine transitLine = schedule.getTransitLines().get(route.getLineId());
				TransitRoute transitRoute = transitLine.getRoutes().get(route.getRouteId());
				
				TransitStopFacility originStop = schedule.getFacilities().get(route.getAccessStopId());
				TransitStopFacility destinationStop = schedule.getFacilities().get(route.getEgressStopId());


				stageResponse.lineName = transitRoute.getDescription();
				stageResponse.originName = originStop.getName();
				stageResponse.destinationName = destinationStop.getName();
				stageResponse.transportMode = transitRoute.getTransportMode();

				Stream<TransitStopFacility> intermediateStops = transitRoute.getStops().stream().map(x -> x.getStopFacility())
						.dropWhile(x -> (!x.equals(originStop))).takeWhile(x -> (!x.equals(destinationStop)));

				intermediateStops = Stream.concat(intermediateStops, Stream.of(destinationStop));

				Stream<CoordinateResponse> path = intermediateStops.map(stop -> {
					Coord stopCoord = stop.getCoord();

					DirectPosition stopCoordinate = new DirectPosition2D(stopCoord.getX(), stopCoord.getY());
					DirectPosition stopWGS48 = new DirectPosition2D();
					try{
						backTransform.transform(stopCoordinate, stopWGS48);
					} catch (TransformException e) {
						throw new RuntimeException(e);
					}
					CoordinateResponse coordinateResponse = new CoordinateResponse();
					coordinateResponse.latitude = stopWGS48.getCoordinate()[0];
					coordinateResponse.longitude = stopWGS48.getCoordinate()[1];
					return coordinateResponse;
				});

				stageResponse.path = path.toList();

				tripResponse.stages.add(stageResponse);
			} else {
				WalkStageResponse stageResponse = new WalkStageResponse();
				stageResponse.departureTime = leg.getDepartureTime().orElse(0);
				stageResponse.arrivalTime = stageResponse.departureTime + leg.getTravelTime().orElse(0);
				stageResponse.distance = leg.getRoute().getDistance();
				tripResponse.stages.add(stageResponse);
			}
		}

		return tripResponse;
	}
}
