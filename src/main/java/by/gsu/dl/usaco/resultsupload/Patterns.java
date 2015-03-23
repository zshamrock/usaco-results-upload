package by.gsu.dl.usaco.resultsupload;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import by.gsu.dl.usaco.resultsupload.domain.Contest;
import by.gsu.dl.usaco.resultsupload.domain.Division;
import by.gsu.dl.usaco.resultsupload.exception.NoMatchesException;
import by.gsu.dl.usaco.resultsupload.exception.ParsingException;

/**
 * <p>
 * Read <a href="http://www.martinfowler.com/bliki/ComposedRegex.html">http://www.martinfowler.com/bliki/ComposedRegex.html</a>
 * about ComposedRegex.
 * </p>
 */
public final class Patterns {
    public static enum Type {
        CONTEST,
        ALLOWED_PARTICIPANT_NAME
    }

    private static final Map<Type, Pattern> PATTERNS = new HashMap<Type, Pattern>() {
        {
            put(Type.CONTEST, Patterns.contestPattern());
            put(Type.ALLOWED_PARTICIPANT_NAME, Patterns.allowedParticipantNamePattern());
        }
    };

    public static Contest matchesContest(final String contest) {
        return matches(Type.CONTEST,
                contest,
                new Function<Matcher, Contest>() {
                    @Override
                    public Contest apply(Matcher matcher) {
                        final int year = Integer.parseInt(matcher.group(1));
                        final String month = matcher.group(2);
                        final Division division = Division.valueOf(matcher.group(3).toUpperCase());
                        return new Contest(year, month, division);
                    }
                });
    }

    public static boolean matchesParticipantName(final String participantName) {
        return participantName.matches(PATTERNS.get(Type.ALLOWED_PARTICIPANT_NAME).pattern());
    }

    private static <T> T matches(final Type type,
                                 final String text,
                                 final Function<Matcher, T> factory) {
        final Pattern pattern = PATTERNS.get(type);
        final Optional<T> result;
        try {
            final Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                result = Optional.of(factory.apply(matcher));
            } else {
                result = Optional.absent();
            }
        } catch (final Exception ex) {
            throw new ParsingException(ex, type, text, pattern);
        }
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new NoMatchesException(type, text, pattern);
        }
    }

    // Final Results: USACO 2014 February Contest, Bronze
    private static Pattern contestPattern() {
        final String finalResults = "Final Results:";
        final String usaco = "USACO";
        final String year = "(\\d{4})";
        final String month = "([a-zA-Z]+)";
        final String contest = "Contest,";
        final String division = "(Bronze|Silver|Gold)(\\s+Division)?";
        return composePattern(finalResults, usaco, year, month, contest, division);
    }

    private static Pattern composePattern(final String... args) {
        final String regex = "^" + String.join("\\s+", args);
        return Pattern.compile(regex);
    }

    private static Pattern allowedParticipantNamePattern() {
        final String englishLetters = "a-zA-Z";
        final String russianLetters = "а-яА-Я";
        final String digits = "0-9";
        final String symbols = ".\\-";
        final String space = " ";

        return Pattern.compile("^[" + englishLetters + russianLetters + digits + symbols + space + "]+$");
    }
}
