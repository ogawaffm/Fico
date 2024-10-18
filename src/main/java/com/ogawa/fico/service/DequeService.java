package com.ogawa.fico.service;

import com.ogawa.fico.multithreading.ExtendedExecutorCompletionService;
import com.ogawa.fico.multithreading.ExtendedFutureTask;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @param <B> Type of the task result Construct a Service to poll tasks from the
 */
@Slf4j
public abstract class DequeService<B> {

    static private final AtomicLong instanceCount = new AtomicLong(0);

    private final Long instanceId = instanceCount.incrementAndGet();

    private final AtomicBoolean isTerminated = new AtomicBoolean(false);

    private final List<ThreadPoolExecutor> producers;

    private final ExtendedExecutorCompletionService<B> executorCompletionService;

    final Consumer<B> consumer;

    /**
     * @param executorCompletionService ExecutorCompletionService to poll tasks from
     * @param producers                 Producers of the tasks of the ExecutorCompletionService
     * @param consumer                  Consumer of the tasks bean
     *                                  <p>
     *                                  ExecutorCompletionService is used to poll beans from. The consumer is used to
     *                                  consume the beans. The producer is used to determine if the production has
     *                                  ended. If the producer is null, there is no stop on production end. Then the
     *                                  service must be stopped by calling stop.
     */
    DequeService(@NonNull ExtendedExecutorCompletionService<B> executorCompletionService,
        @NonNull List<ThreadPoolExecutor> producers, @NonNull Consumer<B> consumer) {
        this.executorCompletionService = executorCompletionService;
        this.producers = producers;
        this.consumer = consumer;
    }

    public void start() {
        log.info("Starting service: {}", getServiceName());
        new Thread(this::run, getServiceName()).start();
    }

    public void stop() {
        log.info("Stopping service: {}", getServiceName());
        isTerminated.set(true);
    }

    public boolean isTerminated() {
        return isTerminated.get();
    }

    public String getServiceName() {
        return this.getClass().getSimpleName() + "-" + instanceId;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    private boolean allProducersTerminated() {
        return producers.stream().allMatch(ThreadPoolExecutor::isTerminated);
    }

    private void run() {

        Future<B> futureTask = null;
        BeanEncapsulatingCallable<B> beanEncapsulatingCallable;

        boolean productionEnded;

        do {

            // determine if production has ended first, then poll to have a delay to place the last task
            productionEnded = allProducersTerminated();

            try {

                futureTask = executorCompletionService.poll(1000, TimeUnit.MILLISECONDS);

                if (futureTask != null) {

                    beanEncapsulatingCallable = (BeanEncapsulatingCallable<B>) ((ExtendedFutureTask<B>) futureTask).getTask();

                    B bean = beanEncapsulatingCallable.getBean();

                    consumer.accept(bean);

                }

            } catch (InterruptedException ignore) {
                log.error("{} was interrupted", getServiceName());
            }


        } while ((!productionEnded || futureTask != null) && !isTerminated());

        if (!isTerminated()) {
            stop();
        }

        log.info("{} stopped", getServiceName());
    }


}
