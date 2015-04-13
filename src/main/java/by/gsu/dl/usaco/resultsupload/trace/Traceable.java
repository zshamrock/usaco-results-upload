package by.gsu.dl.usaco.resultsupload.trace;

public interface Traceable {
    public void trace(final String message, final Object... args);

    public void trace(final String message);
}
