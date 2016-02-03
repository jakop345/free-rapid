package com.subgraph.orchid;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Threading {

    public static ReentrantLock lock(String name) {
        return new ReentrantLock();
    }

    public static ExecutorService newPool(final String name) {
        ThreadFactory factory = createThreadFactory(name);
        return Executors.newCachedThreadPool(factory);
    }

    public static ScheduledExecutorService newSingleThreadScheduledPool(final String name) {
        ThreadFactory factory = createThreadFactory(name);
        return Executors.newSingleThreadScheduledExecutor(factory);
    }

    public static ScheduledExecutorService newScheduledPool(final String name) {
        ThreadFactory factory = createThreadFactory(name);
        return Executors.newScheduledThreadPool(1, factory);
    }

    private static ThreadFactory createThreadFactory(final String name) {
        return new ThreadFactory() {
            private final AtomicInteger num = new AtomicInteger();

            @Override
            public Thread newThread(final Runnable r) {
                Thread t = new Thread(r);
                t.setName(name + "-" + num.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        };
    }
}
