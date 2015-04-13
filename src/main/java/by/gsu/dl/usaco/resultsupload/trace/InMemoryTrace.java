package by.gsu.dl.usaco.resultsupload.trace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InMemoryTrace implements Trace {

    private final Queue<String> queue = new ConcurrentLinkedQueue<String>();

    @Override
    public void add(final String... messages) {
        this.queue.addAll(Arrays.asList(messages));
    }

    @Override
    public List<String> latest(final int n) {
        final List<String> latestN = new ArrayList<String>();
        while (!this.queue.isEmpty()) {
            final String message = this.queue.poll();
            if (message != null) {
                latestN.add(message);
            }
            if (latestN.size() == n) {
                break;
            }
        }
        return latestN;
    }
}
