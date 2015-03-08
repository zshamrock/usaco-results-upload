package by.gsu.dl.usaco.resultsupload;

import java.io.IOException;

import org.jsoup.nodes.Document;

public interface SourceData {
    public Document document() throws IOException;
}
