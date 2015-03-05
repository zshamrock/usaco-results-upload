package by.gsu.dl.usaco.resultsupload.domain;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Participant {
    String country;
    int year;
    String name;
    int score;
    @Singular
    List<Submission> submissions;
}
