package com.github.ulwx.aka.dbutils.tool.support;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class SegmentLock {
    private static Integer segments = 16;//默认分段数量
    private static final HashMap<Integer, ReentrantLock> lockMap = new HashMap<>();

    static {
        init(segments, false);
    }

    private static void init(Integer counts, boolean fair) {
        if (counts != null) {
            segments = counts;
        }
        for (int i = 0; i < segments; i++) {
            lockMap.put(i, new ReentrantLock(fair));
        }
    }

    public static <T> void lock(T key) {
        ReentrantLock lock = lockMap.get((key.hashCode() >>> 1) % segments);
        lock.lock();
    }

    public static <T> void unlock(T key) {
        ReentrantLock lock = lockMap.get((key.hashCode() >>> 1) % segments);
        lock.unlock();
    }

    public static void main(String[] args) {

        String s1 = "12345" + "123";
        String s2 = "12345123";
        System.out.println(s1.hashCode() == s2.hashCode());

    }
}
