package com.sanshengshui.server.common.transport.quota.inmemory;

import com.sanshengshui.server.common.transport.quota.Clock;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author james mu
 * @date 19-1-23 下午3:45
 */
public class IntervalCount {

    private final LongAdder addr = new LongAdder();
    private final long intervalDurationMs;
    private volatile long startTime;
    private volatile long lastTickTime;

    public IntervalCount(long intervalDurationMs){
        this.intervalDurationMs = intervalDurationMs;
        startTime = Clock.millis();
    }

    public long resetIfExpiredAndTick(){
       if (isExpired()){
           reset();
       }
       tick();
       return addr.sum();
    }

    public long silenceDuration(){
        return Clock.millis() - lastTickTime;
    }

    public long getCount(){
        return addr.sum();
    }

    public void tick(){
        addr.add(1);
        lastTickTime = Clock.millis();
    }

    private void reset(){
        addr.reset();
        lastTickTime = Clock.millis();
    }

    private boolean isExpired(){
        return (Clock.millis() - startTime) > intervalDurationMs;
    }
}
