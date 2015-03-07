package by.gsu.dl.usaco.resultsupload.domain;

import lombok.NonNull;
import lombok.Value;

@Value
public class Contest {
    int year;
    @NonNull String month;
    @NonNull Division division;
}
