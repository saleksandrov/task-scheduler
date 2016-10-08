import com.asv.ts.CallableTask;
import com.asv.ts.TaskScheduler;

import java.time.LocalDateTime;

/**
 * Класс запускает планировщик и назначает задачи для выполнения.
 *
 *
 * @author Sergey Aleksandrov
 * @since 08.10.2016
 */
public class Start {

    public static void main(String[] args) {

        final TaskScheduler ts = TaskScheduler.start();

        Runnable task = () -> {
            LocalDateTime localTime = LocalDateTime.now();
            LocalDateTime localDateTimePlus1 = localTime.plusSeconds(1);
            LocalDateTime localDateTimePlus2 = localTime.plusSeconds(2);
            LocalDateTime localDateTimePlus8 = localTime.plusSeconds(8);
            LocalDateTime localDateTimePlus9 = localTime.plusSeconds(9);
            LocalDateTime localDateTimePlus10 = localTime.plusSeconds(10);
            LocalDateTime localDateTimePlus12 = localTime.plusSeconds(12);
            LocalDateTime localDateTimePlus14 = localTime.plusSeconds(14);
            LocalDateTime localDateTimePlus16 = localTime.plusSeconds(16);
            LocalDateTime localDateTimePlus18 = localTime.plusSeconds(18);
            LocalDateTime localDateTimePlus20 = localTime.plusSeconds(20);

            ts.scheduleTask(localDateTimePlus10, new CallableTask(localDateTimePlus10));
            ts.scheduleTask(localDateTimePlus12, new CallableTask(localDateTimePlus12));
            ts.scheduleTask(localDateTimePlus14, new CallableTask(localDateTimePlus14));
            ts.scheduleTask(localDateTimePlus16, new CallableTask(localDateTimePlus16));
            ts.scheduleTask(localDateTimePlus18, new CallableTask(localDateTimePlus18));
            ts.scheduleTask(localDateTimePlus20, new CallableTask(localDateTimePlus20));
            ts.scheduleTask(localDateTimePlus1, new CallableTask(localDateTimePlus1));
            ts.scheduleTask(localDateTimePlus2, new CallableTask(localDateTimePlus2));
            ts.scheduleTask(localDateTimePlus8, new CallableTask(localDateTimePlus8));
            ts.scheduleTask(localDateTimePlus9, new CallableTask(localDateTimePlus9));
        };

        startThreads(1000, task);
        ts.stop(30);
    }

    static void startThreads(int number, Runnable task) {
        while (number-- > 0) {
           new Thread(task).start();
        }

    }

}
