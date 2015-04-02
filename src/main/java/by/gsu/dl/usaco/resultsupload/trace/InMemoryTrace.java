package by.gsu.dl.usaco.resultsupload.trace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InMemoryTrace implements Trace {

    private Queue<String> queue = new ConcurrentLinkedQueue<String>();

    @Override
    public void add(String... messages) {
        queue.addAll(Arrays.asList(messages));
    }

    @Override
    public List<String> latest(int n) {
        List<String> latestN = new ArrayList<String>();
        while (!queue.isEmpty()) {
            final String message = queue.poll();
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
