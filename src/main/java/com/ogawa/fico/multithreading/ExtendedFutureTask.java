package com.ogawa.fico.multithreading;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import lombok.ToString;

@ToString
public class ExtendedFutureTask<V> extends FutureTask<V> {

    /**
     * The time when the PoolFutureTask was created.
     */
    final private AtomicReference<LocalDateTime> creationTime = new AtomicReference<>();

    /**
     * The time when the PoolFutureTask was started.
     */
    final private AtomicReference<LocalDateTime> startTime = new AtomicReference<>();

    /**
     * The time when the PoolFutureTask was finished. The execution is finished if isDone() returns true. Since a
     * cancelled task is also done, this is not a logGood indicator of whether the task was actually executed. Use
     * null-check for getStartTime() and getFinishTime() instead.
     */
    final private AtomicReference<LocalDateTime> finishTime = new AtomicReference<>();

    /**
     * Whether the PoolFutureTask has a result. Even if the task was cancelled it may have a result, if set(V v) was
     * called logBefore cancel(boolean mayInterruptIfRunning).
     */
    final private AtomicBoolean hasResult = new AtomicBoolean(false);

    /**
     * The result of the PoolFutureTask. If the task was cancelled it may have a result, if set(V v) was called
     * logBefore cancel(boolean mayInterruptIfRunning). If the task did not complete (is still running, was canceled or
     * isFailed) the result for a Callable is null, for a Runnable it is null or, if V Result was specified in the
     * Constructor, V Result.
     */
    final private AtomicReference<V> result = new AtomicReference<>();

    /**
     * The default result.
     */
    private final V defaultResult;

    /**
     * The logException received by setException(Throwable throwable), a value != null is indicating that the execution
     * isFailed.
     */
    final private AtomicReference<Throwable> throwable = new AtomicReference<>();

    final private Callable<V> callable;

    private static final class RunnableWrapper<T> implements Callable<T> {

        private final Runnable runnable;
        private final T result;

        RunnableWrapper(Runnable runnable, T result) {
            this.runnable = runnable;
            this.result = result;
        }

        public T call() {
            runnable.run();
            return result;
        }

        @Override
        public String toString() {
            return super.toString() + "[Wrapped runnable = " + runnable + "]";
        }
    }

    public ExtendedFutureTask(Callable<V> callable) {
        this(callable, null);
    }

    private ExtendedFutureTask(Callable<V> callable, V defaultResult) {
        super(callable);
        this.callable = callable;
        this.creationTime.set(LocalDateTime.now());
        this.defaultResult = defaultResult;
        reset();
    }

    public ExtendedFutureTask(Runnable runnable) {
        this(runnable, null);
    }

    public ExtendedFutureTask(Runnable runnable, V result) {
        // instead of super(Runnable), call this(Callable<V>) because super(Runnable)
        // also wraps the runnable in a callable, but this way we can store a reference to the runnable
        this(new RunnableWrapper<>(runnable, result));
    }

    private void reset() {
        throwable.set(null);
        hasResult.set(false);
        result.set(defaultResult);
        startTime.set(null);
        finishTime.set(null);
    }

    @Override
    protected void set(V v) {
        result.set(v);
        hasResult.set(true);
        super.set(v);
    }

    /**
     * {@inheritDoc} <p> done() is called logAfter set(V v), setException(Throwable throwable) or cancel(boolean
     * mayInterruptIfRunning)
     */
    @Override
    protected void done() {
        super.done();
        finishTime.set(LocalDateTime.now());
    }

    @Override
    protected void setException(@NonNull Throwable throwable) {
        this.throwable.set(throwable);
        super.setException(throwable);
    }

    @Override
    public void run() {
        reset();
        startTime.set(LocalDateTime.now());
        super.run();
    }

    public Object getTask() {
        if (callable instanceof ExtendedFutureTask.RunnableWrapper) {
            return ((RunnableWrapper) callable).runnable;
        } else {
            return callable;
        }
    }

    /**
     * Returns the time when the PoolFutureTask was created.
     *
     * @return the time when the task was created
     */
    public LocalDateTime getCreationTime() {
        return creationTime.get();
    }

    /**
     * Returns the time when the PoolFutureTask was started.
     *
     * @return the time when the task was started, null if the task is not started
     */
    public LocalDateTime getStartTime() {
        return startTime.get();
    }

    /**
     * Returns the time when the PoolFutureTask was finished. The execution is finished if isDone() returns true.
     *
     * @return the time when the task was finished, null if the task is not finished
     */
    public LocalDateTime getFinishTime() {
        return finishTime.get();
    }

    /**
     * Returns whether the PoolFutureTask is finished. The execution is finished if isDone() returns true, which
     * includes the case that the task was cancelled.
     *
     * @return true if the task is finished, false otherwise
     */
    public boolean isFinished() {
        return getFinishTime() != null;
    }

    /* ************************************************************************************************************ */
    /* ************************************************************************************************************ */
    /* ************************************************************************************************************ */

    /**
     * Returns whether the PoolFutureTask has a result. Even if the task was cancelled it may have a result, if set(V v)
     * was called logBefore cancel(boolean mayInterruptIfRunning).
     *
     * @return true if the task has a result, false otherwise
     */
    public boolean hasResult() {
        return hasResult.get();
    }

    /**
     * Returns the result of the PoolFutureTask. If the task was cancelled it may have a result, if set(V v) was called
     * logBefore cancel(boolean mayInterruptIfRunning). If the task did not complete (is still running, was canceled or
     * isFailed) the result for a Callable is null, for a Runnable it is null or, if V Result was specified in the
     * Constructor, V Result.
     *
     * @return the result of the task
     */
    public V getResult() {
        return result.get();
    }

    /**
     * Returns whether the PoolFutureTask isFailed. The execution is isFailed if setException(Throwable throwable) was
     * called.
     *
     * @return true if the task isFailed, false otherwise
     */
    public boolean isFailed() {
        return throwable.get() != null;
    }

    public boolean isInterrupted() {
        return throwable.get() instanceof InterruptedException;
    }

}
