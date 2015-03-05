package by.gsu.dl.usaco.resultsupload.domain;

import lombok.Value;

import java.util.List;

@Value
public class Submission {
    String problemName;
    List<Character> tests;
}
