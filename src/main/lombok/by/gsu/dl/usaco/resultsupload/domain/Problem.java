package by.gsu.dl.usaco.resultsupload.domain;

import lombok.NonNull;
import lombok.Value;

@Value
public class Problem {
    @NonNull String name;
    int testsCount;
}
