package com.asv.ts;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Основной класс, осуществляющий запуск задач по рассписанию. Класс является синглтоном. Класс потокобезопасен.
 *
 * @author Sergey Aleksandrov
 * @since 08.10.2016
 */
public class TaskScheduler {

    private static final TaskScheduler taskScheduler = new TaskScheduler();

    private ExecutorService executorService;
    private Thread scheduleThread;
    private final ConcurrentNavigableMap<Long, List<CallableTask>> tasksMap = new ConcurrentSkipListMap<>();
    private final CopyOnWriteArrayList<Future<Long>> results = new CopyOnWriteArrayList<>();

    private TaskScheduler() {

    }

    /**
     * Возвращает результаты выполнения запланированных задач
     *
     * @return
     */
    public Iterator<Future<Long>> getResults() {
        return results.iterator();
    }

    /**
     * Метод выполняет предварительную инициализацию объета и возвращает инстанс класса {@link TaskScheduler}.
     *
     * @return singleton instance
     */
    public static TaskScheduler start() {
        synchronized (taskScheduler) {
            if (taskScheduler.executorService == null) {
                taskScheduler.executorService = Executors.newCachedThreadPool();
            }
            if (taskScheduler.scheduleThread == null || taskScheduler.scheduleThread.isInterrupted()) {
                taskScheduler.scheduleThread = new Thread(taskScheduler::doScheduleWork);
                taskScheduler.scheduleThread.start();
            }
        }
        return taskScheduler;
    }

    /**
     * Метод добавляет в планировщик новую задачу, которая будет запущена в заданное время.
     *
     * @param localDateTime время запуска задачи
     * @param callableTask  задача на выполнение
     */
    public void scheduleTask(LocalDateTime localDateTime, CallableTask callableTask) {
        long timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<CallableTask> callableTasks;
        synchronized (tasksMap) {
            callableTasks = tasksMap.computeIfAbsent(timestamp, (k) -> new LinkedList<>());
            callableTasks.add(callableTask);
        }

    }

    /**
     * Метод останавливает все запущенные потоки
     *
     * @param timeout время ожидания завершения работы потоков
     * @return boolean флаг успеха прерывания работы потоков
     */
    public boolean stop(int timeout) {
        if (executorService != null) {
            try {
                executorService.awaitTermination(timeout, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                return false;
            }
            executorService = null;
        }
        if (scheduleThread != null) {
            scheduleThread.interrupt();
        }
        return true;
    }

    private void doScheduleWork() {
        while (!Thread.currentThread().isInterrupted()) {
            long currentTimestamp = System.currentTimeMillis();
            ConcurrentNavigableMap<Long, List<CallableTask>> tasksToSchedule = tasksMap.headMap(currentTimestamp, true);

            for (Long key : tasksToSchedule.keySet()) {
                List<CallableTask> tasks;
                synchronized (tasksMap) {
                    tasks = tasksToSchedule.remove(key);
                }
                if (tasks == null) {
                    continue;
                }
                for (CallableTask task : tasks) {
                    Future<Long> result = executorService.submit(task);
                    results.add(result);
                }
            }
        }
    }

}
