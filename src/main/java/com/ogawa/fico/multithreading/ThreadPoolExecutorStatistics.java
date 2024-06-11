package com.ogawa.fico.multithreading;

import java.util.concurrent.atomic.AtomicLong;
import lombok.ToString;

@ToString
public class ThreadPoolExecutorStatistics implements ExtendedThreadPoolExecutor.Observer {

    private final AtomicLong submittedCount = new AtomicLong(0);
    private final AtomicLong startedCount = new AtomicLong(0);

    // finished = cancelled + interrupted + failed + succeeded
    private final AtomicLong cancelledCount = new AtomicLong(0);
    private final AtomicLong interruptedCount = new AtomicLong(0);
    private final AtomicLong failedCount = new AtomicLong(0);
    private final AtomicLong succeededCount = new AtomicLong(0);
    private final AtomicLong finishedCount = new AtomicLong(0);

    private final AtomicLong resultCount = new AtomicLong(0);

    /**
     * Returns the number of tasks that have been submitted.
     *
     * @return
     */
    public long getSubmittedCount() {
        return submittedCount.get();
    }

    /**
     * Returns the number of tasks that have been started.
     *
     * @return
     */
    public long getStartedCount() {
        return startedCount.get();
    }

    /**
     * Returns the number of tasks that have been cancelled logBefore or logAfter they were started.
     *
     * @return
     */
    public long getCanceledCount() {
        return cancelledCount.get();
    }

    /**
     * Returns the number of tasks that have been interrupted logAfter they were started.
     *
     * @return
     */
    public long getInterruptedCount() {
        return interruptedCount.get();
    }

    /**
     * Returns the number of tasks that have been failed logAfter they were started.
     *
     * @return
     */
    public long getFailedCount() {
        return failedCount.get();
    }

    /**
     * Returns the number of tasks that have been succeeded logAfter they were started.
     *
     * @return
     */
    public long getSucceededCount() {
        return succeededCount.get();
    }

    /**
     * Returns the number of succeeded tasks that have a result which is not null.
     *
     * @return
     */
    public long getResultCount() {
        return resultCount.get();
    }

    /**
     * Returns the number of tasks that have been finished logAfter they were started.
     *
     * @return
     */
    public long getFinishedCount() {
        return finishedCount.get();
    }

    /**
     * Updates the statistics with the given event for the given ExtendedFutureTask and count.
     *
     * @param extendedFutureTask
     * @param event
     * @param count
     */
    private void update(ExtendedFutureTask extendedFutureTask,
        ExtendedFutureTaskEvent event, long count) {

        switch (event) {
            case SUBMITTED:
                submittedCount.addAndGet(count);
                break;
            case STARTED:
                startedCount.addAndGet(count);
                break;
            case SUCCEEDED:
                succeededCount.addAndGet(count);
                if (extendedFutureTask != null && extendedFutureTask.getResult() != null) {
                    resultCount.addAndGet(count);
                }
                finishedCount.addAndGet(count);
                break;
            case CANCELLED:
                cancelledCount.addAndGet(count);
                if (extendedFutureTask != null && extendedFutureTask.isDone()) {
                    finishedCount.addAndGet(count);
                }
                break;
            case FAILED:
                failedCount.addAndGet(count);
                finishedCount.addAndGet(count);
                break;
            case INTERRUPTED:
                interruptedCount.addAndGet(count);
                finishedCount.addAndGet(count);
                break;
            default:
                throw new IllegalStateException("Unexpected state: " + event);

        }
    }

    /**
     * Updates the statistics with the given event.
     *
     * @return
     */
    @Override
    public void update(ExtendedFutureTask extendedFutureTask, ExtendedFutureTaskEvent event) {
        update(extendedFutureTask, event, 1);
    }

    @Override
    public void update(ExtendedFutureTaskEvent event, long count) {
        update(null, event, count);
    }
}
