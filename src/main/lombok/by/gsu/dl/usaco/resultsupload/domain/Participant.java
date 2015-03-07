package by.gsu.dl.usaco.resultsupload.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Participant {
    @NonNull String country;
    int year;
    @NonNull String name;
    int score;
    List<Submission> submissions;
}
