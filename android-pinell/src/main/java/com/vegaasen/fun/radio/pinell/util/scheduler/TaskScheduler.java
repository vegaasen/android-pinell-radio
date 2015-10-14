package com.vegaasen.fun.radio.pinell.util.scheduler;

import android.os.Handler;
import android.support.v4.util.ArrayMap;

/**
 * @author <a href="vegaasen@gmail.com">vegardaasen</a>
 */
public class TaskScheduler extends Handler {

    private ArrayMap<Runnable, Runnable> tasks = new ArrayMap<>();

    public void scheduledAtSpecificTime(final Runnable task, final long when) {
        postAtTime(task, when);
    }

    public void scheduleAtFixedRate(final Runnable task, long delay, final long period) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                task.run();
                postDelayed(this, period);
            }
        };
        tasks.put(task, runnable);
        postDelayed(runnable, delay);
    }

    public void scheduleAtFixedRate(final Runnable task, final long period) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                task.run();
                postDelayed(this, period);
            }
        };
        tasks.put(task, runnable);
        runnable.run();
    }

    public void stop(Runnable task) {
        Runnable removed = tasks.remove(task);
        if (removed != null) removeCallbacks(removed);
    }

}