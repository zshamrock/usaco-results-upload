package by.gsu.dl.usaco.resultsupload.trace;

import java.util.List;

/**
 * Implementation must be <strong>Thread Safe!</strong>
 */
public interface Trace {
    public static final int LATEST_ALL = -1;

    /**
     * Add messages into the trace log.
     */
    public void add(String... messages);

    /**
     * @return latest <code>n</code> messages (they are returned and removed from the trace log).
     *      If <code>n</code> is {@link #LATEST_ALL}, then return all messages from the trace log.
     */
    public List<String> latest(int n);
}
