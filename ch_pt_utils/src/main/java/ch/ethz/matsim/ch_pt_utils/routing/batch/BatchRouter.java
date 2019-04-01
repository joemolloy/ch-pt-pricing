package ch.ethz.matsim.ch_pt_utils.routing.batch;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.matsim.core.utils.misc.Time;

import ch.ethz.matsim.ch_pt_utils.routing.PlanRoutingRequest;
import ch.ethz.matsim.ch_pt_utils.routing.PlanRoutingResult;
import ch.ethz.matsim.ch_pt_utils.routing.router.Router;
import ch.ethz.matsim.ch_pt_utils.routing.router.RouterFactory;

public class BatchRouter {
	private final static Logger logger = Logger.getLogger(BatchRouter.class);

	private final RouterFactory factory;
	private int numberOfRunners;
	private int batchSize;

	public BatchRouter(RouterFactory factory, int numberOfRunners, int batchSize) {
		this.factory = factory;
		this.numberOfRunners = numberOfRunners;
		this.batchSize = batchSize;
	}

	public void run(Iterator<PlanRoutingRequest> requestIterator, Consumer<PlanRoutingResult> resultConsumer)
			throws InterruptedException {
		run(requestIterator, resultConsumer, Optional.empty());
	}

	public void run(Iterator<PlanRoutingRequest> requestIterator, Consumer<PlanRoutingResult> resultConsumer,
			int numberOfRequests) throws InterruptedException {
		run(requestIterator, resultConsumer, Optional.of(numberOfRequests));
	}

	private void run(Iterator<PlanRoutingRequest> requestIterator, Consumer<PlanRoutingResult> resultConsumer,
			Optional<Integer> numberOfRequests) throws InterruptedException {
		List<Thread> threads = new LinkedList<>();
		AtomicInteger progressCounter = new AtomicInteger(0);

		for (int i = 0; i < numberOfRunners; i++) {
			Thread thread = new Thread(() -> {
				runInstance(requestIterator, resultConsumer, progressCounter);
			});

			thread.setName("BatchRouter." + (i + 1));
			thread.start();

			threads.add(thread);
		}

		AtomicBoolean isFinished = new AtomicBoolean(false);

		Thread progressThread = new Thread(() -> {
			try {
				runProgress(progressCounter, isFinished, numberOfRequests);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
		progressThread.setName("BatchRouter.Progress");
		progressThread.start();

		for (Thread thread : threads) {
			thread.join();
		}

		isFinished.set(true);
		progressThread.join();
	}

	private void runProgress(AtomicInteger progressCounter, AtomicBoolean isFinished,
			Optional<Integer> numberOfRequests) throws InterruptedException {
		long lastProgress = 0;
		long startTime = System.nanoTime();

		while (!isFinished.get()) {
			long currentProgress = progressCounter.get();

			if (currentProgress > lastProgress) {
				lastProgress = currentProgress;

				long currentTime = System.nanoTime();
				double runtime = (currentTime - startTime) * 1e-9;

				if (numberOfRequests.isPresent()) {
					double progress = (double) currentProgress / numberOfRequests.get();

					double progressPerSecond = (double) currentProgress / runtime;
					double eta = (numberOfRequests.get() - currentProgress) / progressPerSecond;

					logger.info(String.format("Routing %d/%d (%.2f%%, Runtime %s, ETA %s)", currentProgress,
							numberOfRequests.get(), 100.0 * progress, Time.writeTime(runtime), Time.writeTime(eta)));
				} else {
					logger.info(String.format("Routing %d (Runtime %s)", currentProgress, Time.writeTime(runtime)));
				}
			}

			Thread.sleep(1000);
		}

		double totalTime = (System.nanoTime() - startTime) * 1e-9;
		logger.info(String.format("Routing finished after %s", Time.writeTime(totalTime)));
	}

	private void runInstance(Iterator<PlanRoutingRequest> requestIterator, Consumer<PlanRoutingResult> resultConsumer,
			AtomicInteger progressCounter) {
		Router router = factory.createRouter();

		while (true) {
			List<PlanRoutingRequest> requests = new LinkedList<>();

			synchronized (requestIterator) {
				while (requestIterator.hasNext() && requests.size() < batchSize) {
					requests.add(requestIterator.next());
				}
			}

			if (requests.size() == 0) {
				return;
			}

			List<PlanRoutingResult> results = new LinkedList<>();

			for (PlanRoutingRequest request : requests) {
				results.add(router.process(request));
				progressCounter.incrementAndGet();
			}

			synchronized (resultConsumer) {
				results.forEach(resultConsumer::accept);
			}
		}
	}
}
