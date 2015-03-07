package by.gsu.dl.usaco.resultsupload;

import by.gsu.dl.usaco.resultsupload.domain.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HTMLResults {

    // see http://en.wikipedia.org/wiki/Latin-1_Supplement_(Unicode_block)
    // unicode value for HTML &nbsp; (is part of LATIN_1_SUPPLEMENT)
    private static final String NON_BREAKING_SPACE_UNICODE = "\u00a0";

    private static final int PARTICIPANT_COUNTRY_INDEX = 0;
    private static final int PARTICIPANT_YEAR_INDEX = 1;
    private static final int PARTICIPANT_NAME_INDEX = 2;
    private static final int PARTICIPANT_SCORE_INDEX = 3;

    private final Document document;
    private Element body;
    private Header header;
    private List<Problem> problems;
    private List<Participant> participants;

    public HTMLResults(SourceData source) throws IOException {
        document = source.document();
        body = document.body();
        collectHeader();
        collectProblems();
        collectParticipants(problems());
    }

    private void collectHeader() {
        header = Patterns.matchesHeader(body.select("h1").first().text());
    }

    private void collectProblems() {
        final Element participants = body.select("table").first();
        final List<Element> problemsElements = participants.select("tbody tr").first().select("th[colspan]");
        problems = problemsElements.stream()
                .map(el -> new Problem(el.text(), Integer.parseInt(el.attr("colspan")) - 1))
                .collect(Collectors.toList());
    }

    private void collectParticipants(List<Problem> problems) {
        final Elements participantsElements = body.select("table").first().select("tbody tr:gt(0)");
        participants = participantsElements.stream()
                .map(tr -> {
                    final Elements tds = tr.select("td");
                    return Participant.builder()
                            .country(tds.get(PARTICIPANT_COUNTRY_INDEX).text())
                            .year(Integer.parseInt(tds.get(PARTICIPANT_YEAR_INDEX).text().replaceAll(NON_BREAKING_SPACE_UNICODE, "").trim()))
                            .name(tds.get(PARTICIPANT_NAME_INDEX).text())
                            .score(Integer.parseInt(tds.get(PARTICIPANT_SCORE_INDEX).text()))
                            .submissions(Collections.<Submission>emptyList())
                            .build();
                }).collect(Collectors.toList());
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

    public List<Participant> participants() {
        return participants;
    }
}
