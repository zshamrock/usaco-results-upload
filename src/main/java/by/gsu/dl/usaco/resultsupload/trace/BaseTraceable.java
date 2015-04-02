package by.gsu.dl.usaco.resultsupload.trace;

import java.util.Locale;
import java.util.ResourceBundle;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;

public class BaseTraceable implements Traceable {

    private static final String BUNDLE_NAME = "trace";
    private static final Object[] NO_ARGS = new Object[]{};

    private final Optional<Trace> trace;
    private final ResourceBundle bundle;

    public BaseTraceable(final Optional<Trace> trace, Locale locale) {
        this.bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        this.trace = trace;
    }

    @Override
    public void trace(String message, Object... args) {
        if (trace.isPresent()) {
            if (bundle.containsKey(message)) {
                trace.get().add(String.format(new String(
                        bundle.getString(message).getBytes(Charsets.ISO_8859_1), Charsets.UTF_8), args));
            }
        }
    }

    @Override
    public void trace(String message) {
        trace(message, NO_ARGS);
    }
}
