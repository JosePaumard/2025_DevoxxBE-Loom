package org.paumard.loom.vthreads;

import java.util.ArrayList;

public class B_JumpingVirtualThread {

    void main() throws InterruptedException {

        final int SLEEP_TIME = 20;
        final int N_THREADS = 10;

        Runnable printAndBlock = () -> {
            System.out.println(Thread.currentThread());
            sleepFor(SLEEP_TIME);
            System.out.println(Thread.currentThread());
            sleepFor(SLEEP_TIME);
            System.out.println(Thread.currentThread());
            sleepFor(SLEEP_TIME);
            System.out.println(Thread.currentThread());
        };
        Runnable block = () -> {
            sleepFor(SLEEP_TIME);
            sleepFor(SLEEP_TIME);
            sleepFor(SLEEP_TIME);
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

        for (var thread : threads) {
            thread.start();
        }

        for (var thread : threads) {
            thread.join();
        }
    }

    private static void sleepFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
