package org.axonframework.axonserver.connector.heartbeat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Responsible to scheduler at fixed rate a generic task.
 * The scheduling can be started an stopped at any time.
 *
 * @author Sara Pellegrini
 * @since 4.2.1
 */
public class Scheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

    private static final long DEFAULT_INITIAL_DELAY = 10_000;

    private static final long DEFAULT_RATE = 1_000;

    private final ScheduledExecutorService scheduler;

    private final long initialDelay;

    private final long rate;

    private final AtomicReference<ScheduledFuture> scheduledTask = new AtomicReference<>();

    private final Runnable runnable;

    /**
     * Constructs an instance of a {@link Scheduler} using a default initial delay of 10 seconds,
     * a default scheduling period of 1 second and a single thread {@link ScheduledExecutorService}.
     *
     * @param runnable the task to be scheduled
     */
    public Scheduler(Runnable runnable) {
        this(runnable, Executors.newSingleThreadScheduledExecutor());
    }

    /**
     * Constructs an instance of a {@link Scheduler} using a default initial delay of 10 seconds
     * and a default scheduling period of 1 second.
     * @param runnable the task to be scheduled
     * @param scheduler the {@link ScheduledExecutorService} to use for scheduling the task
     */
    public Scheduler(Runnable runnable, ScheduledExecutorService scheduler) {
        this(runnable, scheduler, DEFAULT_INITIAL_DELAY, DEFAULT_RATE);
    }

    /**
     * Primary constructor for {@link Scheduler}
     * @param runnable the task to be scheduled
     * @param scheduler the {@link ScheduledExecutorService} to use for scheduling the task
     * @param initialDelay the initial delay, in milliseconds
     * @param rate the scheduling period, in milliseconds
     */
    public Scheduler(Runnable runnable,
                     ScheduledExecutorService scheduler,
                     long initialDelay,
                     long rate) {
        this.runnable = runnable;
        this.scheduler = scheduler;
        this.initialDelay = initialDelay;
        this.rate = rate;
    }

    /**
     * Schedule the specified task accordingly with the specified scheduling setting.
     */
    public void start() {
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(runnable,
                                                                initialDelay,
                                                                rate,
                                                                MILLISECONDS);
        if (!scheduledTask.compareAndSet(null, task)) {
            task.cancel(true);
            LOGGER.warn("{} already scheduled.", runnable.getClass().getSimpleName());
        }
    }

    /**
     * Interrupt the scheduled task if running and stop all the following scheduled executions.
     */
    public void stop() {
        ScheduledFuture<?> task = scheduledTask.get();
        if (task != null && scheduledTask.compareAndSet(task, null)) {
            task.cancel(true);
        } else {
            LOGGER.warn("{} already stopped.", runnable.getClass().getSimpleName());
        }
    }
}
