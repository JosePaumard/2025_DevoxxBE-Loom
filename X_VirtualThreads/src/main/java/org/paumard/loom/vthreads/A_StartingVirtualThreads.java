package org.paumard.loom.vthreads;

public class A_StartingVirtualThreads {

    void main() throws InterruptedException {

        Runnable task =
              () -> System.out.println("platform: " +
                    Thread.currentThread());

        // platform thread
        Thread t1 = Thread.ofPlatform()
              .unstarted(task);
        t1.start();
        t1.join();

        // virtual thread
        Thread t2 = Thread.ofVirtual()
              .unstarted(task);
        t2.start();
        t2.join();
    }
}













