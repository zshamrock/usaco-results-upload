package by.gsu.dl.usaco.resultsupload;

import java.io.IOException;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.google.common.base.Optional;

import by.gsu.dl.usaco.resultsupload.trace.BaseTraceable;
import by.gsu.dl.usaco.resultsupload.trace.Trace;
import by.gsu.dl.usaco.resultsupload.trace.Traceable;

public class NetworkSourceData extends BaseTraceable implements SourceData, Traceable {
    private static final int MAX_BODY_SIZE_5_MB_IN_BYTES = 5 * 1024 * 1024;

    private final String url;

    public NetworkSourceData(final String url, final Optional<Trace> trace, final Locale locale) {
        super(trace, locale);
        this.url = url;
    }

    @Override
    public Document document() throws IOException {
        this.trace("reading.document", this.url);
        return Jsoup.connect(this.url).maxBodySize(MAX_BODY_SIZE_5_MB_IN_BYTES).get();
    }
}
