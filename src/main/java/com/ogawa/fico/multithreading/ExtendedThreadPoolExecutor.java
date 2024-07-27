package com.ogawa.fico.multithreading;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import lombok.NonNull;
import lombok.ToString;

/**
 * I1 {@link ThreadPoolExecutor} that can be paused and resumed.
 */
@ToString
public class ExtendedThreadPoolExecutor extends ThreadPoolExecutor {

    private boolean isPaused = false;
    private final ReentrantLock pauseLock = new ReentrantLock();
    private final Condition unpaused = pauseLock.newCondition();

    // set of observers in a predictable order (LinkedHashSet)
    private final Set<Observer> observers = new LinkedHashSet<>();

    int before = 0;
    int after = 0;

    public ExtendedThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
        long keepAliveTime, @NonNull TimeUnit unit,
        @NonNull String poolNamePrefix, @NonNull String threadNamePrefix) {

        super(corePoolSize, maximumPoolSize,
            keepAliveTime, unit,
            new PriorityBlockingQueue<>(),
            new ExtendedThreadFactory(poolNamePrefix, threadNamePrefix)
        );
    }

    public ExtendedThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
        long keepAliveTime, @NonNull TimeUnit unit,
        @NonNull BlockingQueue workQueue, @NonNull ThreadFactory threadFactory) {

        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    interface Observer {

        // update for event of distinct ExtendedFutureTask
        void update(ExtendedFutureTask extendedFutureTask, ExtendedFutureTaskEvent event);

        // update for event of multiple ExtendedFutureTask (Collection)
        void update(ExtendedFutureTaskEvent event, long count);
    }

    public void registerObserver(@NonNull Observer observer) {
        observers.add(observer);
    }

    public void unregisterObserver(@NonNull Observer observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(ExtendedFutureTask extendedFutureTask, ExtendedFutureTaskEvent event) {
        observers.forEach(observer -> observer.update(extendedFutureTask, event));
    }

    protected void notifyObservers(ExtendedFutureTaskEvent event, long count) {
        observers.forEach(observer -> observer.update(event, count));
    }

    @Override
    protected <T> ExtendedFutureTask<T> newTaskFor(Runnable runnable, T value) {
        return new ExtendedFutureTask<T>(runnable, value);
    }

    @Override
    protected <T> ExtendedFutureTask<T> newTaskFor(Callable<T> callable) {
        return new ExtendedFutureTask<>(callable);
    }

    @Override

    protected void beforeExecute(Thread thread, Runnable runnable) {

        super.beforeExecute(thread, runnable);

        before++;

        ifPausedWaitUntilResume(thread);

        if (runnable instanceof ExtendedFutureTask<?>) {
            notifyObservers((ExtendedFutureTask) runnable, ExtendedFutureTaskEvent.STARTED);
        }
    }

    protected void afterExecute(Runnable runnable, Throwable throwable) {

        super.afterExecute(runnable, throwable);
        after++;

        if (runnable instanceof ExtendedFutureTask<?>) {
            notifyObservers(
                (ExtendedFutureTask) runnable,
                ExtendedFutureTaskEvent.getState((ExtendedFutureTask) runnable)
            );
        }
    }

    @Override
    public <V> ExtendedFutureTask<V> submit(Runnable task, V result) {
        ExtendedFutureTask<V> extendedFutureTask = (ExtendedFutureTask<V>) super.submit(task, result);
        notifyObservers(extendedFutureTask, ExtendedFutureTaskEvent.SUBMITTED);
        return extendedFutureTask;
    }

    public ExtendedFutureTask<?> submit(Runnable task) {
        ExtendedFutureTask<?> extendedFutureTask = (ExtendedFutureTask<?>) super.submit(task);
        notifyObservers(extendedFutureTask, ExtendedFutureTaskEvent.SUBMITTED);
        return extendedFutureTask;
    }

    @Override
    public <V> ExtendedFutureTask<V> submit(Callable<V> task) {
        ExtendedFutureTask<V> extendedFutureTask = (ExtendedFutureTask<V>) super.submit(task);
        notifyObservers(extendedFutureTask, ExtendedFutureTaskEvent.SUBMITTED);
        return extendedFutureTask;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        notifyObservers(ExtendedFutureTaskEvent.SUBMITTED, tasks.size());
        return super.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
        long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        notifyObservers(ExtendedFutureTaskEvent.SUBMITTED, tasks.size());
        return super.invokeAny(tasks, timeout, unit);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        notifyObservers(ExtendedFutureTaskEvent.SUBMITTED, tasks.size());
        return super.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
        long timeout, TimeUnit unit) throws InterruptedException {
        notifyObservers(ExtendedFutureTaskEvent.SUBMITTED, tasks.size());
        return super.invokeAll(tasks, timeout, unit);
    }

    private void ifPausedWaitUntilResume(Thread t) {
        pauseLock.lock();
        try {
            while (isPaused) {
                unpaused.await();
            }
        } catch (InterruptedException ie) {
            t.interrupt();
        } finally {
            pauseLock.unlock();
        }
    }

    public void pause() {
        pauseLock.lock();
        try {
            isPaused = true;
        } finally {
            pauseLock.unlock();
        }
    }

    public void resume() {
        pauseLock.lock();
        try {
            isPaused = false;
            unpaused.signalAll();
        } finally {
            pauseLock.unlock();
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

}
