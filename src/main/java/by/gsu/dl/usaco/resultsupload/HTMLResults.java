package by.gsu.dl.usaco.resultsupload;

import by.gsu.dl.usaco.resultsupload.domain.Division;
import by.gsu.dl.usaco.resultsupload.domain.Header;
import by.gsu.dl.usaco.resultsupload.domain.Problem;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HTMLResults {

    private final Document document;
    private final Header header;
    private Element body;
    private final List<Problem> problems;

    public HTMLResults(SourceData source) throws IOException {
        document = source.document();
        body = document.body();
        header = Patterns.matchesHeader(body.select("h1").first().text());

        final Element participants = body.select("table").first();
        final List<Element> problemsElements = participants.select("tbody tr").first().select("th[colspan]");
        problems = problemsElements.stream()
                .map(el -> new Problem(el.text(), Integer.parseInt(el.attr("colspan")) - 1))
                .collect(Collectors.toList());
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

    public List<Problem> problems() {
        return problems;
    }


}
