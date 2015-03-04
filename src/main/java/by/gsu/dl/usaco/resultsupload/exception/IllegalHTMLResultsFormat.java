package by.gsu.dl.usaco.resultsupload.exception;

public class IllegalHTMLResultsFormat extends RuntimeException {

    public enum Element {
        HEADER
    }

    public IllegalHTMLResultsFormat(Element element) {
        super(element.name());
    }
}
