package com.github.ulwx.aka.dbutils.tool.support;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class SegmentLock {
    private volatile Integer segments = 16;//默认分段数量
    private  final HashMap<Integer, ReentrantLock> lockMap = new HashMap<>();

    public  SegmentLock(){
        this(16,false);
    }
    public  SegmentLock(Integer counts, boolean fair) {
        if (counts != null) {
            segments = counts;
        }
        for (int i = 0; i < segments; i++) {
            lockMap.put(i, new ReentrantLock(fair));
        }
    }

    public  <T> void lock(T key) {
        ReentrantLock lock = lockMap.get((key.hashCode() >>> 1) % segments);
        lock.lock();
    }

    public  <T> void unlock(T key) {
        ReentrantLock lock = lockMap.get((key.hashCode() >>> 1) % segments);
        lock.unlock();
    }


}
