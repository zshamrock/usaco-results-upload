package by.gsu.dl.usaco.resultsupload;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import by.gsu.dl.usaco.resultsupload.domain.Contest;
import by.gsu.dl.usaco.resultsupload.domain.Division;
import by.gsu.dl.usaco.resultsupload.domain.Participant;
import by.gsu.dl.usaco.resultsupload.domain.Problem;
import by.gsu.dl.usaco.resultsupload.domain.Submission;
import by.gsu.dl.usaco.resultsupload.exception.HTMLResultsCreationException;
import by.gsu.dl.usaco.resultsupload.trace.BaseTraceable;
import by.gsu.dl.usaco.resultsupload.trace.Trace;
import by.gsu.dl.usaco.resultsupload.trace.Traceable;

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
 *
 * @checkstyle JavadocVariable
 */
public class HTMLResults extends BaseTraceable implements Traceable {

    private static final Logger LOGGER = Logger.getLogger(HTMLResults.class);

    public static final int OBSERVER_YEAR = 9999;

    enum ParticipantType {
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
    private Document document;
    private Contest contest;
    private List<Problem> problems;
    private List<Participant> preCollegeParticipants;
    private List<Participant> observers;

    /**
     * @checkstyle IllegalCatch
     */
    public HTMLResults(final SourceData source, final Optional<Trace> trace, final Locale locale) {
        super(trace, locale);
        try {
            LOGGER.info("Getting a document");
            this.document = source.document();
            this.body = this.document.body();
            this.collectData();
            trace("summary.results", this.year(), this.month(), this.division(), this.preCollegeParticipants().size(),
                    this.observers().size());
        } catch (final Exception ex) {
            if (ex instanceof HttpStatusException) {
                final HttpStatusException httpStatusException = (HttpStatusException) ex;
                trace("error.http", httpStatusException.getStatusCode(), httpStatusException.getUrl());
            } else {
                trace("error.processing");
            }
            LOGGER.error("Failed processing HTML results", ex);

            throw new HTMLResultsCreationException(ex);
        }
    }

    private void collectData() {
        // Order of calls is important!
        // We could pragmatically enforce and check it via internal state machine, but is it worth it?
        // Could be done as an improvement.
        this.collectContest();
        this.collectProblems();
        this.collectPreCollegeParticipants();
        this.collectObservers();
    }

    private void collectContest() {
        this.trace("collecting.contest");
        this.contest = Patterns.matchesContest(this.body.select("h1").first().text());
    }

    private void collectProblems() {
        this.trace("collecting.problems");
        final List<Element> headerCells = this.preCollegeParticipantsTable().select("tbody tr").first().select("th[colspan]");
        this.problems = Collections.unmodifiableList(
                Lists.transform(headerCells, new Function<Element, Problem>() {
                    @Override
                    public Problem apply(final Element headerCell) {
                        return new Problem(headerCell.text(), Integer.parseInt(headerCell.attr("colspan")) - 1); // one cell is used for spacing
                    }
                }));
    }

    private void collectPreCollegeParticipants() {
        this.trace("collecting.precollegeparticipants");
        this.preCollegeParticipants = this.collectParticipants(this.preCollegeParticipantsTable(),
                ParticipantType.PRE_COLLEGE);
    }

    private Element preCollegeParticipantsTable() {
        return this.body.select("table").first();
    }

    private void collectObservers() {
        this.trace("collecting.observers");
        this.observers = this.collectParticipants(this.observersTable(), ParticipantType.OBSERVER);
    }

    private Element observersTable() {
        return this.body.select("table").last();
    }

    private List<Participant> collectParticipants(final Element participantsTable,
                                                  final ParticipantType participantType) {
        final Elements participantsRows = participantsTable.select("tbody tr:gt(0)");
        return Collections.unmodifiableList(
                Lists.transform(participantsRows, new Function<Element, Participant>() {
                    @Override
                    public Participant apply(final Element participantRow) {
                        final Elements participantCells = HTMLResults.this.selectParticipantCellsFrom(participantRow);
                        return Participant.builder()
                                .country(HTMLResults.this.getParticipantCountryFrom(participantCells))
                                .year(HTMLResults.this.getParticipantYearFrom(participantCells, participantType))
                                .name(HTMLResults.this.getParticipantNameFrom(participantCells, participantType))
                                .score(HTMLResults.this.getParticipantScoreFrom(participantCells, participantType))
                                .submissions(HTMLResults.this.collectSubmissions(participantCells, participantType))
                                .build();
                    }
                }));
    }

    private Elements selectParticipantCellsFrom(final Element participantRow) {
        return participantRow.select("td");
    }

    private String getParticipantCountryFrom(final Elements participantCells) {
        return participantCells.get(PARTICIPANT_COUNTRY_INDEX).text();
    }

    private int getParticipantYearFrom(final Elements participantCells, final ParticipantType participantType) {
        return participantType == ParticipantType.PRE_COLLEGE
                ? Integer.parseInt(participantCells.get(PARTICIPANT_YEAR_INDEX).text()
                .replaceAll(NON_BREAKING_SPACE_UNICODE, "").trim())
                : OBSERVER_YEAR;
    }

    private String getParticipantNameFrom(final Elements participantCells, final ParticipantType participantType) {
        return participantCells.get(participantNameIndex(participantType)).text();
    }

    private int getParticipantScoreFrom(final Elements participantCells, final ParticipantType participantType) {
        return Integer.parseInt(participantCells.get(participantScoreIndex(participantType)).text());
    }

    private static int participantNameIndex(final ParticipantType participantType) {
        return participantType == ParticipantType.PRE_COLLEGE
                ? PARTICIPANT_NAME_INDEX
                : PARTICIPANT_NAME_INDEX - 1;
    }

    private static int participantScoreIndex(final ParticipantType participantType) {
        return participantType == ParticipantType.PRE_COLLEGE
                ? PARTICIPANT_SCORE_INDEX
                : PARTICIPANT_SCORE_INDEX - 1;
    }

    private List<Submission> collectSubmissions(
            final Elements participantCells, final ParticipantType participantType) {
        final List<Submission> submissions = new ArrayList<Submission>(this.problems.size());
        final Deque<int[]> submissionsFromTo = new ArrayDeque<int[]>(this.problems.size());
        submissionsFromTo.push(new int[]{
                participantSubmissionsStartIndex(participantType),
                participantSubmissionsStartIndex(participantType) + this.problems.get(0).getTestsCount()});
        for (int i = 1; i < this.problems.size(); i++) {
            if (i == 0) {
                continue; // skip first
            }
            final int lastTo = submissionsFromTo.getLast()[1];
            final int newFrom = lastTo + EMPTY_CELLS_COUNT_BETWEEN_PROBLEMS_SUBMISSIONS; // skipping extra empty cells
            submissionsFromTo.addLast(new int[]{newFrom, newFrom + this.problems.get(i).getTestsCount()});
        }
        final Queue<Problem> problemsQueue = new LinkedList<Problem>(this.problems);
        final Collection<List<Element>> participantSubCells = Collections2.transform(submissionsFromTo, new Function<int[], List<Element>>() {
            @Override
            public List<Element> apply(final int[] fromTo) {
                return participantCells.subList(fromTo[0], fromTo[1]);
            }
        });
        for (final List<Element> cells : participantSubCells) {
            String submission = "";
            for (final Element cell : cells) {
                final String text = cell.text();
                submission += text.isEmpty() ? " " : text;
            }
            submissions.add(new Submission(problemsQueue.poll().getName(), submission));
        }
        return Collections.unmodifiableList(submissions);
    }

    private static int participantSubmissionsStartIndex(final ParticipantType participantType) {
        return participantType == ParticipantType.PRE_COLLEGE
                ? PARTICIPANT_SUBMISSIONS_START_INDEX
                : PARTICIPANT_SUBMISSIONS_START_INDEX - 1;
    }

    public int year() {
        return this.contest.getYear();
    }

    /**
     * @return either month as it is (like January), or US Open (which is usually April)
     */
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

    public void saveTo(final String path) throws IOException {
        final PrintWriter out = new PrintWriter(path);
        try {
            out.println(this.document.outerHtml());
        } finally {
            out.close();
        }
    }
}
