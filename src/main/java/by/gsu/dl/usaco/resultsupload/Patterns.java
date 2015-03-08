package by.gsu.dl.usaco.resultsupload;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        CONTEST
    }

    private static final Map<Type, Pattern> PATTERNS = new HashMap<Type, Pattern>() {
        {
            put(Type.CONTEST, Patterns.contestPattern());
        }
    };

    public static Contest matchesContest(final String contest) {
        return matches(Type.CONTEST,
                contest,
                (matcher) -> {
                    final int year = Integer.parseInt(matcher.group(1));
                    final String month = matcher.group(2);
                    final Division division = Division.valueOf(matcher.group(3).toUpperCase());
                    return new Contest(year, month, division);
                }
        );
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
                result = Optional.empty();
            }
        } catch (final Exception ex) {
            throw new ParsingException(ex, type, text, pattern);
        }
        return result.orElseThrow(() -> new NoMatchesException(type, text, pattern));
    }

    // Final Results: USACO 2014 February Contest, Bronze
    private static Pattern contestPattern() {
        final String finalResults = "Final Results:";
        final String usaco = "USACO";
        final String year = "(\\d{4})";
        final String month = "([a-zA-Z]+)";
        final String contest = "Contest,";
        final String division = "(Bronze|Silver|Gold)";
        return composePattern(finalResults, usaco, year, month, contest, division);
    }

    private static Pattern composePattern(final String... args) {
        final String regex = "^" + String.join("\\s+", args);
        return Pattern.compile(regex);
    }
}
