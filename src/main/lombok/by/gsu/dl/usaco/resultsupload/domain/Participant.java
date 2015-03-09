package by.gsu.dl.usaco.resultsupload.domain;

import java.util.List;

import lombok.Builder;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

@Value
@Builder
@ToString(exclude = "submissions")
public class Participant {
    @NonNull String country;
    int year;
    @NonNull String name;
    int score;
    List<Submission> submissions;
}
