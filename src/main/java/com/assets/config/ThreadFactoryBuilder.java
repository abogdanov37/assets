package com.assets.config;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadFactoryBuilder {
    private static final String DEFAULT_PREFIX = "default-thread";
    private String namePrefix = null;
    private boolean daemon = false;
    private int priority = Thread.NORM_PRIORITY;
    private ThreadFactory backingThreadFactory = null;
    private UncaughtExceptionHandler uncaughtExceptionHandler = null;

    public ThreadFactoryBuilder setNamePrefix(String namePrefix) {
        if (namePrefix == null) {
            namePrefix = DEFAULT_PREFIX;
        }
        this.namePrefix = namePrefix;
        return this;
    }

    public ThreadFactoryBuilder setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public ThreadFactoryBuilder setPriority(int priority) {
        if (priority < Thread.MIN_PRIORITY) {
            throw new IllegalArgumentException(String.format(
                    "Thread priority (%s) must be >= %s", priority,
                    Thread.MIN_PRIORITY));
        }

        if (priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException(String.format(
                    "Thread priority (%s) must be <= %s", priority,
                    Thread.MAX_PRIORITY));
        }

        this.priority = priority;
        return this;
    }

    public ThreadFactoryBuilder setUncaughtExceptionHandler(
            UncaughtExceptionHandler uncaughtExceptionHandler) {
        if (null == uncaughtExceptionHandler) {
            throw new IllegalArgumentException(
                    "UncaughtExceptionHandler cannot be null");
        }
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        return this;
    }

    public ThreadFactoryBuilder setThreadFactory(
            ThreadFactory backingThreadFactory) {
        if (null == backingThreadFactory) {
            throw new IllegalArgumentException(
                    "BackingThreadFactory cannot be null");
        }
        this.backingThreadFactory = backingThreadFactory;
        return this;
    }

    public ThreadFactory build() {
        ThreadFactory factory = (backingThreadFactory != null) ? backingThreadFactory
                : Executors.defaultThreadFactory();
        return new BioThreadFactory(namePrefix, daemon, priority, uncaughtExceptionHandler, factory);
    }

    private class BioThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final Boolean daemon;
        private final Integer priority;
        private final UncaughtExceptionHandler uncaughtExceptionHandler;
        private final ThreadFactory backingThreadFactory;

        private final AtomicLong count = new AtomicLong(0);

        public BioThreadFactory(String namePrefix, Boolean daemon, Integer priority
                , UncaughtExceptionHandler uncaughtExceptionHandler, ThreadFactory backingThreadFactory) {
            this.namePrefix = namePrefix;
            this.daemon = daemon;
            this.priority = priority;
            this.uncaughtExceptionHandler = uncaughtExceptionHandler;
            this.backingThreadFactory = backingThreadFactory;
        }

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = backingThreadFactory.newThread(runnable);
            if (namePrefix != null) {
                thread.setName(namePrefix + "-" + count.getAndIncrement());
            }
            thread.setDaemon(daemon);
            thread.setPriority(priority);

            if (uncaughtExceptionHandler != null) {
                thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
            }
            return thread;
        }
    }
}
