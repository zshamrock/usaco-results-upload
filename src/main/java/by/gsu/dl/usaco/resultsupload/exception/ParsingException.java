package by.gsu.dl.usaco.resultsupload.exception;

import java.util.regex.Pattern;

import by.gsu.dl.usaco.resultsupload.Patterns;

public class ParsingException extends RuntimeException {
    public ParsingException(final Exception ex, final Patterns.Type type, final String text, final Pattern pattern) {
        super(String.format("Parsing exception [%s] on %s for %s", text, pattern.pattern(), type), ex);
    }
}
