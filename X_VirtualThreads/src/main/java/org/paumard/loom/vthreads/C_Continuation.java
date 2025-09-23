package org.paumard.loom.vthreads;

import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;

public class C_Continuation {

    // --add-exports java.base/jdk.internal.vm=ALL-UNNAMED
    void main() {

        var scope = new ContinuationScope("hello");
        Continuation continuation = new Continuation(
              scope,
              () -> {
                  System.out.println("I am in the continuation");
                  Continuation.yield(scope);
                  System.out.println("I am still in the continuation");
              }
        );

        System.out.println("Main");
        continuation.run();
        System.out.println("Back");
        continuation.run();
        System.out.println("Back again");
    }
}
