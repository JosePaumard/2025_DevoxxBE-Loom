package org.paumard.loom.vthreads;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class D_HowManyPThreads {

    public static final Pattern POOL_PATTERN =
          Pattern.compile("ForkJoinPool-[\\d?]+");
    public static final Pattern WORKER_PATTERN =
          Pattern.compile("worker-[\\d?]+");

    void main() throws InterruptedException {
        Set<String> poolNames = ConcurrentHashMap.newKeySet();
        Set<String> pThreadNames = ConcurrentHashMap.newKeySet();

        var threads = IntStream.range(0, 1_000_000)
              .mapToObj(_ -> Thread.ofVirtual()
                    .unstarted(
                          () -> {
                            String poolName = readPoolName();
                            poolNames.add(poolName);
                            String workerName = readWorkerName();
                            pThreadNames.add(workerName);
                    }
                    )
              )
              .toList();

        Instant begin = Instant.now();
        for (var thread : threads) {
            thread.start();
        }
        for (var thread : threads) {
            thread.join();
        }
        Instant end = Instant.now();

        System.out.println("# cores = " + Runtime.getRuntime().availableProcessors());
        System.out.println("Time = " + Duration.between(begin, end).toMillis() + "ms");
        System.out.println("### Pools");
        poolNames.forEach(System.out::println);
        System.out.println("### Platform threads (" + pThreadNames.size() + ")");
        new TreeSet<>(pThreadNames).forEach(System.out::println);
    }


    private static String readWorkerName() {
        String name = Thread.currentThread().toString();
        Matcher workerMatcher = WORKER_PATTERN.matcher(name);
        if (workerMatcher.find()) {
            return workerMatcher.group();
        }
        return "not found";
    }

    private static String readPoolName() {
        String name = Thread.currentThread().toString();
        Matcher poolMatcher = POOL_PATTERN.matcher(name);
        if (poolMatcher.find()) {
            return poolMatcher.group();
        }
        return "pool not found";
    }
}
