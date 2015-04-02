package by.gsu.dl.usaco.resultsupload.trace;

import com.google.common.base.Optional;

public class BaseTraceable implements Traceable {
    private static final Object[] NO_ARGS = new Object[]{};

    private final Optional<Trace> trace;

    public BaseTraceable(final Optional<Trace> trace) {
        this.trace = trace;
    }

    @Override
    public void trace(String message, Object... args) {
       if (trace.isPresent())  {
           trace.get().add(String.format(message, args));
       }
    }

    @Override
    public void trace(String message) {
        trace(message, NO_ARGS);
    }
}
