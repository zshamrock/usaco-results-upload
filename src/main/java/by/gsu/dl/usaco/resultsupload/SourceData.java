package by.gsu.dl.usaco.resultsupload;

import org.jsoup.nodes.Document;

import java.io.IOException;

public interface SourceData {
    public Document document() throws IOException;
}
