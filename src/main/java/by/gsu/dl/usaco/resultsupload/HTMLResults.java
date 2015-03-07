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
    private Contest contest;
    private List<Problem> problems;
    private List<Participant> participants;

    public HTMLResults(SourceData source) throws IOException {
        document = source.document();
        body = document.body();
        collectContest();
        collectProblems();
        collectParticipants(problems());
    }

    private void collectContest() {
        contest = Patterns.matchesContest(body.select("h1").first().text());
    }

    private void collectProblems() {
        final List<Element> headerCells = participantsTable().select("tbody tr").first().select("th[colspan]");
        problems = headerCells.stream()
                .map(th -> new Problem(th.text(), Integer.parseInt(th.attr("colspan")) - 1)) // one cell is used for spacing
                .collect(Collectors.toList());
    }

    private Element participantsTable() {
        return body.select("table").first();
    }

    private void collectParticipants(List<Problem> problems) {
        final Elements participantsRows = participantsTable().select("tbody tr:gt(0)");
        participants = participantsRows.stream()
                .map(tr -> {
                    final Elements participantsCells = tr.select("td");
                    return Participant.builder()
                            .country(participantsCells.get(PARTICIPANT_COUNTRY_INDEX).text())
                            .year(Integer.parseInt(participantsCells.get(PARTICIPANT_YEAR_INDEX).text()
                                    .replaceAll(NON_BREAKING_SPACE_UNICODE, "").trim()))
                            .name(participantsCells.get(PARTICIPANT_NAME_INDEX).text())
                            .score(Integer.parseInt(participantsCells.get(PARTICIPANT_SCORE_INDEX).text()))
                            .submissions(Collections.<Submission>emptyList())
                            .build();
                }).collect(Collectors.toList());
    }

    public int year() {
        return contest.getYear();
    }

    public String month() {
        return contest.getMonth();
    }

    public Division division() {
        return contest.getDivision();
    }

    public List<Problem> problems() {
        return problems;
    }

    public List<Participant> participants() {
        return participants;
    }
}
