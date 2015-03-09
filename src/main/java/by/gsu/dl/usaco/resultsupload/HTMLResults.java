package by.gsu.dl.usaco.resultsupload;

import static by.gsu.dl.usaco.resultsupload.HTMLResults.ParticipantType.OBSERVER;
import static by.gsu.dl.usaco.resultsupload.HTMLResults.ParticipantType.PRE_COLLEGE;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import by.gsu.dl.usaco.resultsupload.domain.Contest;
import by.gsu.dl.usaco.resultsupload.domain.Division;
import by.gsu.dl.usaco.resultsupload.domain.Participant;
import by.gsu.dl.usaco.resultsupload.domain.Problem;
import by.gsu.dl.usaco.resultsupload.domain.Submission;

/**
 * <p>
 * <pre>
 * <tr><td>USA</td><td>&nbsp; 2017&nbsp; &nbsp;  </td><td>Peter Wu</td><td>515</td><td>   </td><td>*</td><td>x</td><td>x</td><td>x</td><td>x</td><td>x</td><td>x</td><td>x</td><td>x</td><td>x</td><td>  </td><td>   </td><td>*</td>...</tr>
 *          ^                  ^           ^                  ^              ^                       ^                                                                                                        ^
 *         [0]                [1]          |                 [2]            [3]                     [5]                                                                                                       |
 *          |                  | NON_BREAKING_SPACE_UNICODE   |              |                       |                                                                                   EMPTY_CELLS_COUNT_BETWEEN_PROBLEMS_SUBMISSIONS
 * PARTICIPANT_COUNTRY_INDEX PARTICIPANT_NAME_INDEX  PARTICIPANT_NAME_INDEX PARTICIPANT_SCORE_INDEX PARTICIPANT_SUBMISSIONS_START_INDEX
 *     </pre>
 * </p>
 * <p>
 * For Observers we shift index one to the left, as there is no year for Observers,
 * except country as it comes before year.
 * </p>
 */
public class HTMLResults {

    public static final int OBSERVER_YEAR = 9999;

    static enum ParticipantType {
        PRE_COLLEGE,
        OBSERVER
    }

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

    private Element body;
    private Contest contest;
    private List<Problem> problems;
    private List<Participant> preCollegeParticipants;
    private List<Participant> observers;

    public HTMLResults(final SourceData source) throws IOException {
        this.body = source.document().body();
        collectContest();
        collectProblems();
        collectPreCollegeParticipants(problems());
        collectObservers(problems());
    }

    private void collectContest() {
        this.contest = Patterns.matchesContest(this.body.select("h1").first().text());
    }

    private void collectProblems() {
        final List<Element> headerCells = preCollegeParticipantsTable().select("tbody tr").first().select("th[colspan]");
        this.problems = headerCells.stream()
                .map(headerCell -> new Problem(headerCell.text(), Integer.parseInt(headerCell.attr("colspan")) - 1)) // one cell is used for spacing
                .collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    private void collectPreCollegeParticipants(final List<Problem> problems) {
        this.preCollegeParticipants = collectParticipants(preCollegeParticipantsTable(), problems, PRE_COLLEGE);
    }

    private Element preCollegeParticipantsTable() {
        return this.body.select("table").first();
    }

    private void collectObservers(final List<Problem> problems) {
        this.observers = collectParticipants(observersTable(), problems, OBSERVER);
    }

    private Element observersTable() {
        return this.body.select("table").last();
    }

    private List<Participant> collectParticipants(
            final Element participantsTable, final List<Problem> problems, final ParticipantType participantType) {
        final Elements participantsRows = participantsTable.select("tbody tr:gt(0)");
        return participantsRows.stream()
                .map(participantRow -> {
                    final Elements participantCells = selectParticipantCellsFrom(participantRow);
                    return Participant.builder()
                            .country(getParticipantCountryFrom(participantCells))
                            .year(getParticipantYearFrom(participantCells, participantType))
                            .name(getParticipantNameFrom(participantCells, participantType))
                            .score(getParticipantScoreFrom(participantCells, participantType))
                            .submissions(collectSubmissions(participantCells, problems, participantType))
                            .build();
                }).collect(collectingAndThen(toList(), Collections::unmodifiableList));
    }

    private Elements selectParticipantCellsFrom(Element participantRow) {
        return participantRow.select("td");
    }

    private String getParticipantCountryFrom(Elements participantCells) {
        return participantCells.get(PARTICIPANT_COUNTRY_INDEX).text();
    }

    private int getParticipantYearFrom(Elements participantCells, ParticipantType participantType) {
        return participantType == PRE_COLLEGE
                ? Integer.parseInt(participantCells.get(PARTICIPANT_YEAR_INDEX).text()
                .replaceAll(NON_BREAKING_SPACE_UNICODE, "").trim())
                : OBSERVER_YEAR;
    }

    private String getParticipantNameFrom(Elements participantCells, ParticipantType participantType) {
        return participantCells.get(participantNameIndex(participantType)).text();
    }

    private int getParticipantScoreFrom(Elements participantCells, ParticipantType participantType) {
        return Integer.parseInt(participantCells.get(participantScoreIndex(participantType)).text());
    }

    private static int participantNameIndex(final ParticipantType participantType) {
        return participantType == PRE_COLLEGE
                ? PARTICIPANT_NAME_INDEX
                : PARTICIPANT_NAME_INDEX - 1;
    }

    private static int participantScoreIndex(final ParticipantType participantType) {
        return participantType == PRE_COLLEGE
                ? PARTICIPANT_SCORE_INDEX
                : PARTICIPANT_SCORE_INDEX - 1;
    }

    private List<Submission> collectSubmissions(
            final Elements participantCells, final List<Problem> problems, final ParticipantType participantType) {
        final List<Submission> submissions = new ArrayList<>(problems.size());
        final Deque<int[]> submissionsFromTo = new ArrayDeque<>(problems.size());
        submissionsFromTo.push(new int[]{
                participantSubmissionsStartIndex(participantType),
                participantSubmissionsStartIndex(participantType) + problems.get(0).getTestsCount()});
        problems.stream().skip(1).forEach(problem -> {
            final int lastTo = submissionsFromTo.getLast()[1];
            final int newFrom = lastTo + EMPTY_CELLS_COUNT_BETWEEN_PROBLEMS_SUBMISSIONS; // skipping extra empty cells
            submissionsFromTo.addLast(new int[]{newFrom, newFrom + problem.getTestsCount()});
        });
        final Queue<Problem> problemsQueue = new LinkedList<>(problems);
        submissionsFromTo.stream()
                .map((int[] fromTo) -> participantCells.subList(fromTo[0], fromTo[1]))
                .forEach((List<Element> cells) -> {
                    String submission = "";
                    for (final Element cell : cells) {
                        final String text = cell.text();
                        submission += text.isEmpty() ? " " : text;
                    }
                    submissions.add(new Submission(problemsQueue.poll().getName(), submission));
                });
        return Collections.unmodifiableList(submissions);
    }

    private static int participantSubmissionsStartIndex(final ParticipantType participantType) {
        return participantType == PRE_COLLEGE
                ? PARTICIPANT_SUBMISSIONS_START_INDEX
                : PARTICIPANT_SUBMISSIONS_START_INDEX - 1;
    }

    public int year() {
        return this.contest.getYear();
    }

    public String month() {
        return this.contest.getMonth();
    }

    public Division division() {
        return this.contest.getDivision();
    }

    public List<Problem> problems() {
        return this.problems;
    }

    public List<Participant> preCollegeParticipants() {
        return this.preCollegeParticipants;
    }

    public List<Participant> observers() {
        return this.observers;
    }
}
