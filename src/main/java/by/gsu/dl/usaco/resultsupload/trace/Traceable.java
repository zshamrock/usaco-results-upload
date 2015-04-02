package by.gsu.dl.usaco.resultsupload.trace;

public interface Traceable {
    public void trace(final String message, Object... args);

    public void trace(final String message);
}
