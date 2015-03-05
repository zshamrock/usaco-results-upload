package by.gsu.dl.usaco.resultsupload.exception;

import by.gsu.dl.usaco.resultsupload.Patterns;

import java.util.regex.Pattern;

public class NoMatchesException extends RuntimeException {

    public NoMatchesException(Patterns.Type type, String text, Pattern pattern) {
        super(String.format("Failed to match [%s] on %s for %s", text, pattern.pattern(), type));
    }
}
