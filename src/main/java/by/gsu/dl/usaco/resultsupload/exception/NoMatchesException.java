package by.gsu.dl.usaco.resultsupload.exception;

import java.util.regex.Pattern;

import by.gsu.dl.usaco.resultsupload.Patterns;

public class NoMatchesException extends RuntimeException {

    public NoMatchesException(final Patterns.Type type, final String text, final Pattern pattern) {
        super(String.format("Failed to match [%s] on %s for %s", text, pattern.pattern(), type));
    }
}
