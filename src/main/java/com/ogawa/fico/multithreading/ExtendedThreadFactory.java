package com.ogawa.fico.multithreading;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ExtendedThreadFactory implements ThreadFactory {

    private static final ConcurrentHashMap<String, AtomicInteger> poolToMaxId = new ConcurrentHashMap<>();

    private final AtomicInteger threadNumber = new AtomicInteger(0);

    private final String poolName;

    private final String newThreadNamePrefix;

    private ExtendedThreadFactory() {
        this("inaccessible", "constructor");
    }

    public ExtendedThreadFactory(String poolNamePrefix, String threadNamePrefix) {

        AtomicInteger idZero = new AtomicInteger(0);
        AtomicInteger maxPoolId = poolToMaxId.putIfAbsent(poolNamePrefix, idZero);

        // Wasn't the thread group already known?
        if (maxPoolId == null) {
            // yes, so invoke a new one starting with 0 as base for the (below incremented) first thread id
            maxPoolId = idZero;
        }

        poolName = poolNamePrefix + "-" + maxPoolId.getAndIncrement();

        newThreadNamePrefix = poolName + ":" + threadNamePrefix + "-";

    }

    public String getPoolName() {
        return poolName;
    }

    public Thread newThread(Runnable r) {

        Thread t = new Thread(null, r, newThreadNamePrefix + threadNumber.getAndIncrement(), 0);

        if (t.isDaemon()) {
            t.setDaemon(false);
        }

        if (t.getPriority() != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY);
        }

        return t;
    }
}