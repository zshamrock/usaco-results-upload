package by.gsu.dl.usaco.resultsupload;

import by.gsu.dl.usaco.resultsupload.domain.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *     <pre>
 * <tr><td>USA</td><td>&nbsp; 2017&nbsp; &nbsp;  </td><td>Peter Wu</td><td>515</td><td>   </td><td>*</td><td>x</td><td>x</td><td>x</td><td>x</td><td>x</td><td>x</td><td>x</td><td>x</td><td>x</td><td>  </td><td>   </td><td>*</td>
 *          ^                  ^           ^                  ^              ^                       ^                                                                                                        ^
 *         [0]                [1]          |                 [2]            [3]                     [5]                                                                                                       |
 *          |                  | NON_BREAKING_SPACE_UNICODE   |              |                       |                                                                                   EMPTY_CELLS_COUNT_BETWEEN_PROBLEMS_SUBMISSIONS
 * PARTICIPANT_COUNTRY_INDEX PARTICIPANT_NAME_INDEX  PARTICIPANT_NAME_INDEX PARTICIPANT_SCORE_INDEX PARTICIPANT_SUBMISSIONS_START_INDEX
 *     </pre>
 * </p>
 */
public class HTMLResults {

    // see http://en.wikipedia.org/wiki/Latin-1_Supplement_(Unicode_block)
    // unicode value for HTML &nbsp; (is part of LATIN_1_SUPPLEMENT)
    private static final String NON_BREAKING_SPACE_UNICODE = "\u00a0";

    private static final int PARTICIPANT_COUNTRY_INDEX = 0;
    private static final int PARTICIPANT_YEAR_INDEX = 1;
    private static final int PARTICIPANT_NAME_INDEX = 2;
    private static final int PARTICIPANT_SCORE_INDEX = 3;
    private static final int PARTICIPANT_SUBMISSIONS_START_INDEX = 5;
    // <td>x</td><td>  </td><td>   </td><td>*</td> <-- extra empty cells between problems submissions
    private static final int EMPTY_CELLS_COUNT_BETWEEN_PROBLEMS_SUBMISSIONS = 2;

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
                .map(participantRow -> {
                    final Elements participantCells = participantRow.select("td");
                    return Participant.builder()
                            .country(participantCells.get(PARTICIPANT_COUNTRY_INDEX).text())
                            .year(Integer.parseInt(participantCells.get(PARTICIPANT_YEAR_INDEX).text()
                                    .replaceAll(NON_BREAKING_SPACE_UNICODE, "").trim()))
                            .name(participantCells.get(PARTICIPANT_NAME_INDEX).text())
                            .score(Integer.parseInt(participantCells.get(PARTICIPANT_SCORE_INDEX).text()))
                            .submissions(collectSubmissions(participantCells, problems))
                            .build();
                }).collect(Collectors.toList());
    }

    private List<Submission> collectSubmissions(Elements participantCells, List<Problem> problems) {
        final List<Submission> submissions = new ArrayList<>(problems.size());
        final Deque<int[]> submissionsFromTo = new ArrayDeque<>(problems.size());
        submissionsFromTo.push(new int[] {
                PARTICIPANT_SUBMISSIONS_START_INDEX,
                PARTICIPANT_SUBMISSIONS_START_INDEX + problems.get(0).getTestsCount()});
        problems.stream().skip(1).forEach(problem -> {
            final int lastTo = submissionsFromTo.getLast()[1];
            final int newFrom = lastTo + EMPTY_CELLS_COUNT_BETWEEN_PROBLEMS_SUBMISSIONS; // skipping extra empty cells
            submissionsFromTo.addLast(new int[] {newFrom, newFrom + problem.getTestsCount()});
        });
        final Queue<Problem> problemsQueue = new LinkedList<>(problems);
        submissionsFromTo.stream()
                .map((int[] fromTo) -> participantCells.subList(fromTo[0], fromTo[1]))
                .forEach((List<Element> cells) -> {
                    String submission = "";
                    for (Element cell : cells) {
                        final String text = cell.text();
                        submission += text.isEmpty() ? " " : text;
                    }
                    submissions.add(new Submission(problemsQueue.poll().getName(), submission));
                });

        return Collections.unmodifiableList(submissions);
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
