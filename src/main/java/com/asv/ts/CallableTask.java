package com.asv.ts;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс, представляющий собой, выполняемую планировщиком задачу.
 *
 * @author Sergey Aleksandrov
 * @since 08.10.2016
 */
public class CallableTask implements Callable<Long> {

    private static AtomicInteger count = new AtomicInteger(0);

    private final long plannedTimestamp;

    public CallableTask(LocalDateTime dateTime) {
        this.plannedTimestamp = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public Long call() throws Exception {
        long executedTimestamp = System.currentTimeMillis();
        System.out.println(String.format("%d. Difference between planed and real time is %d milliseconds", count.incrementAndGet(), executedTimestamp - plannedTimestamp));
        return executedTimestamp;
    }
}
