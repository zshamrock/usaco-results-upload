package by.gsu.dl.usaco.resultsupload;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NetworkSourceData implements SourceData {
    private static final int MAX_BODY_SIZE_5_MB_IN_BYTES = 5 * 1024 * 1024;

    private final String url;

    public NetworkSourceData(final String url) {
        this.url = url;
    }

    @Override
    public Document document() throws IOException {
        return Jsoup.connect(this.url).maxBodySize(MAX_BODY_SIZE_5_MB_IN_BYTES).get();
    }
}
