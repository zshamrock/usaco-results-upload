package by.gsu.dl.usaco.resultsupload;

import by.gsu.dl.usaco.resultsupload.domain.Division;
import by.gsu.dl.usaco.resultsupload.domain.Header;
import by.gsu.dl.usaco.resultsupload.exception.IllegalHTMLResultsFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 *     Read <a href="http://www.martinfowler.com/bliki/ComposedRegex.html">http://www.martinfowler.com/bliki/ComposedRegex.html</a>
 *     about ComposedRegex.
 * </p>
 */
public final class Patterns {
    private static Map<String, Pattern> patterns = new HashMap<>();

    public static Header matchesHeader(String header) {
        return matches(
                headerPattern(),
                header,
                (matcher) -> {
                    final int year = Integer.parseInt(matcher.group(1));
                    final String month = matcher.group(2);
                    final Division division = Division.valueOf(matcher.group(3).toUpperCase());
                    return new Header(year, month, division);
                },
                IllegalHTMLResultsFormat.Element.HEADER
        );
    }

    private static <T> T matches(Pattern pattern,
                                 String text,
                                 Function<Matcher, T> factory,
                                 IllegalHTMLResultsFormat.Element element) {
        final IllegalHTMLResultsFormat illegalHTMLResultsFormat = new IllegalHTMLResultsFormat(element);
        final Optional<T> result;
        try {
            final Matcher matcher = pattern.matcher(text);
            if (matcher.matches()) {
                result = Optional.of(factory.apply(matcher));
            } else {
                result = Optional.empty();
            }
        } catch (Exception ex) {
            throw illegalHTMLResultsFormat;
        }
        return result.orElseThrow(() -> illegalHTMLResultsFormat);
    }

    // Final Results: USACO 2014 February Contest, Bronze
    private static Pattern headerPattern() {
        return patterns.computeIfAbsent("header", key -> {
            final String finalResults = "Final Results:";
            final String usaco = "USACO";
            final String year = "(\\d{4})";
            final String month = "([a-zA-Z]+)";
            final String contest = "Contest,";
            final String division = "(Bronze|Silver|Gold)";
            return composePattern(finalResults, usaco, year, month, contest, division);
        });
    }

    private static Pattern composePattern(String... args) {
        final String regex = "^" + String.join("\\s+", args);
        return Pattern.compile(regex);
    }
}
