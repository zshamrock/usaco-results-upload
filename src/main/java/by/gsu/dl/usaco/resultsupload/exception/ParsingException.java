package by.gsu.dl.usaco.resultsupload.exception;

import by.gsu.dl.usaco.resultsupload.Patterns;

import java.util.regex.Pattern;

public class ParsingException extends RuntimeException {
    public ParsingException(Exception ex, Patterns.Type type, String text, Pattern pattern) {
        super(String.format("Parsing exception [%s] on %s for %s", text, pattern.pattern(), type), ex);
    }
}
