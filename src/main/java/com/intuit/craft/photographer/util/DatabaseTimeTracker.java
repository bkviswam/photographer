package com.intuit.craft.photographer.util;

public class DatabaseTimeTracker {
    private static final ThreadLocal<Long> databaseTime = ThreadLocal.withInitial(() -> 0L);

    public static void addDatabaseTime(long time) {
        databaseTime.set(databaseTime.get() + time);
    }

    public static long getDatabaseTime() {
        return databaseTime.get();
    }

    public static void clear() {
        databaseTime.remove();
    }
}
