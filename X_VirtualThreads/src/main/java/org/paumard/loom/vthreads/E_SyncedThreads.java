package org.paumard.loom.vthreads;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class E_SyncedThreads {

    public static final Pattern WORKER_PATTERN =
          Pattern.compile("worker-[\\d?]+");

    private final static Object lock = new Object();
    private static int counter = 0;

    void main() throws InterruptedException {

        Set<String> pThreadNames = ConcurrentHashMap.newKeySet();

        final int SLEEP_TIME = 20;
        final int N_THREADS = 100;

        Runnable printAndBlock = () -> {
            System.out.println(Thread.currentThread());
            synchronized (lock) {
                counter++;
                sleepFor(SLEEP_TIME);
            }
            System.out.println(Thread.currentThread());
            synchronized (lock) {
                counter++;
                sleepFor(SLEEP_TIME);
            }
            System.out.println(Thread.currentThread());
            synchronized (lock) {
                counter++;
                sleepFor(SLEEP_TIME);
            }
            System.out.println(Thread.currentThread());
        };
        Runnable block = () -> {
            synchronized (lock) {
                counter++;
                sleepFor(SLEEP_TIME);
            }
            synchronized (lock) {
                counter++;
                sleepFor(SLEEP_TIME);
            }
            synchronized (lock) {
                counter++;
                sleepFor(SLEEP_TIME);
            }
        };

        var threads = new ArrayList<Thread>();
        threads.add(
              Thread.ofVirtual().unstarted(printAndBlock)
        );
        for (int index = 1; index < N_THREADS; index++) {
            threads.add(
                  Thread.ofVirtual().unstarted(block)
            );
        }

        Instant begin = Instant.now();
        for (var thread : threads) {
            thread.start();
        }

        for (var thread : threads) {
            thread.join();
        }

        Instant end = Instant.now();
        synchronized (lock) {
            System.out.println("# counter = " + counter);
        }
        System.out.println("Duration = " + Duration.between(begin, end).toMillis() + "ms");
        System.out.println("# p threads = " + pThreadNames.size());
    }

    private static String readWorkerName() {
        String name = Thread.currentThread().toString();
        Matcher workerMatcher = WORKER_PATTERN.matcher(name);
        if (workerMatcher.find()) {
            return workerMatcher.group();
        }
        return "not found";
    }

    private static void sleepFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
