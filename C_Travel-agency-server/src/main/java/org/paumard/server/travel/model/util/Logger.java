package org.paumard.server.travel.model.util;

import java.util.Objects;
import java.util.function.Supplier;

public final class Logger {
  private static final ScopedValue<Boolean> DEBUG = ScopedValue.newInstance();

  public static <R, X extends Throwable> R debugCall(ScopedValue.CallableOp<? extends R, X> callableOp) throws X {
    Objects.requireNonNull(callableOp);
    return ScopedValue.where(DEBUG, true).call(callableOp);
  }

  public static void logDebug(Supplier<String> supplier) {
    Objects.requireNonNull(supplier);
    if (DEBUG.isBound() && DEBUG.get()) {
      System.err.println(supplier.get());
    }
  }
}
