package by.gsu.dl.usaco.resultsupload;

import by.gsu.dl.usaco.resultsupload.domain.Division;
import by.gsu.dl.usaco.resultsupload.domain.Header;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class HTMLResults {

    private final Document document;
    private final Header header;
    private Element body;

    public HTMLResults(SourceData source) throws IOException {
        document = source.document();
        body = document.body();
        header = Patterns.matchesHeader(body.select("h1").first().text());
    }

    public int year() {
        return header.getYear();
    }

    public String month() {
        return header.getMonth();
    }

    public Division division() {
        return header.getDivision();
    }


}
