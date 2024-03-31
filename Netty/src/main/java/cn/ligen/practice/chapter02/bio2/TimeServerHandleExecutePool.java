package cn.ligen.practice.chapter02.bio2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author ligen
 * @date 2024/3/27 21:55
 * @description
 */
public class TimeServerHandleExecutePool {

    private ExecutorService executorService;

    public TimeServerHandleExecutePool(int maxPoolSize, int queueSize) {
        executorService = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(), maxPoolSize, 120L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(queueSize)
        );
    }

    public void execute(Runnable task) {
        executorService.execute(task);
    }
}
