package by.gsu.dl.usaco.resultsupload;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NetworkSourceData implements SourceData {
    private final String url;

    public NetworkSourceData(final String url) {
        this.url = url;
    }

    @Override
    public Document document() throws IOException {
        return Jsoup.connect(this.url).get();
    }
}
