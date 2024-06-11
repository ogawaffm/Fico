package com.ogawa.fico.multithreading;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;

public class ExtendedExecutorCompletionService<V> implements CompletionService<V> {

    private final ExtendedThreadPoolExecutor extendedThreadPoolExecutor;
    private final BlockingQueue<ExtendedFutureTask<V>> completionQueue;

    /**
     * FutureTask extension to enqueue upon completion.
     */
    private static class QueueingFuture<V> extends ExtendedFutureTask<V> {

        QueueingFuture(ExtendedFutureTask<V> task, BlockingQueue<ExtendedFutureTask<V>> completionQueue) {
            super(task, null);
            this.task = task;
            this.completionQueue = completionQueue;
        }

        private final ExtendedFutureTask<V> task;
        private BlockingQueue<ExtendedFutureTask<V>> completionQueue;

        protected void done() {
            completionQueue.add(task);
            completionQueue = null;
        }
    }

    /**
     * Creates an ExecutorCompletionService using the supplied extendedThreadPoolExecutor for base task execution and a
     * {@link LinkedBlockingQueue} as a completion queue.
     *
     * @param extendedThreadPoolExecutor the extendedThreadPoolExecutor to invoke
     * @throws NullPointerException if extendedThreadPoolExecutor is {@code null}
     */
    public ExtendedExecutorCompletionService(@NonNull ExtendedThreadPoolExecutor extendedThreadPoolExecutor) {
        this.extendedThreadPoolExecutor = extendedThreadPoolExecutor;
        this.completionQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Creates an ExecutorCompletionService using the supplied executor for base task execution and the supplied queue
     * as its completion queue.
     *
     * @param extendedThreadPoolExecutor the executor to invoke
     * @param completionQueue            the queue to invoke as the completion queue normally one dedicated for invoke
     *                                   by this service. This queue is treated as unbounded -- failed attempted
     *                                   {@code Queue.plus} operations for completed tasks cause them not to be
     *                                   retrievable.
     * @throws NullPointerException if executor or completionQueue are {@code null}
     */
    public ExtendedExecutorCompletionService(
        @NonNull ExtendedThreadPoolExecutor extendedThreadPoolExecutor,
        @NonNull BlockingQueue<ExtendedFutureTask<V>> completionQueue) {

        this.extendedThreadPoolExecutor = extendedThreadPoolExecutor;
        this.completionQueue = completionQueue;
    }

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public ExtendedFutureTask<V> submit(@NonNull Callable<V> task) {
        ExtendedFutureTask<V> f = extendedThreadPoolExecutor.newTaskFor(task);
        extendedThreadPoolExecutor.execute(new QueueingFuture<>(f, completionQueue));
        return f;
    }

    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public ExtendedFutureTask<V> submit(@NonNull Runnable task, V result) {
        ExtendedFutureTask<V> f = extendedThreadPoolExecutor.newTaskFor(task, result);
        extendedThreadPoolExecutor.execute(new QueueingFuture<>(f, completionQueue));
        return f;
    }

    public ExtendedFutureTask<V> take() throws InterruptedException {
        return completionQueue.take();
    }

    public ExtendedFutureTask<V> poll() {
        return completionQueue.poll();
    }

    public ExtendedFutureTask<V> poll(long timeout, TimeUnit unit) throws InterruptedException {
        return completionQueue.poll(timeout, unit);
    }

}
