package by.gsu.dl.usaco.resultsupload.domain;

import lombok.NonNull;
import lombok.Value;

@Value
public class Submission {
    @NonNull String problemName;
    @NonNull String submission;
}
