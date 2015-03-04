package by.gsu.dl.usaco.resultsupload;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileSourceData implements SourceData {

    private final File file;

    public FileSourceData(String pathname) {
        this.file = new File(pathname);
        if (!this.file.exists()) {
            // fail fast
            throw new IllegalArgumentException(String.format("Provided file %s doesn't exist", pathname));
        }
    }
    @Override
    public Document document() throws IOException {
        return Jsoup.parse(this.file, StandardCharsets.UTF_8.name());
    }
}
