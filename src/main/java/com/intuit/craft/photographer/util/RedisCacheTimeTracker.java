package com.intuit.craft.photographer.util;

public class RedisCacheTimeTracker {
    private static final ThreadLocal<Long> cacheTime = ThreadLocal.withInitial(() -> 0L);
    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

    public static void startCacheOperation() {
        startTime.set(System.currentTimeMillis());
    }

    public static void stopCacheOperation() {
        long operationTime = System.currentTimeMillis() - startTime.get();
        cacheTime.set(cacheTime.get() + operationTime);
    }

    public static long getCacheTime() {
        return cacheTime.get();
    }

    public static void clear() {
        cacheTime.remove();
        startTime.remove();
    }
}