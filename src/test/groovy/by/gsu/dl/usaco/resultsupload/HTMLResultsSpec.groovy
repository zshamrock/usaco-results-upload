package by.gsu.dl.usaco.resultsupload

import spock.lang.*

import static by.gsu.dl.usaco.resultsupload.domain.Division.BRONZE

@Stepwise
class HTMLResultsSpec extends Specification {
    @Subject
    @Shared
    def results = new HTMLResults(new FileSourceData(getClass().getResource("/feb14_bronze.html").file))

    def "get year, month and division"() {
        expect:
        results.year() == 2014
        results.month() == "February"
        results.division() == BRONZE
    }

    def "verify there are 3 problems"() {
        expect:
        results.problems().size() == 3
    }

    @Unroll
    def "get problem #name"() {
        setup:
        def problems = results.problems()

        expect:
        problems[i].name == name
        problems[i].testsCount == testsCount

        where:
        i || name | testsCount
        0 || "mirror" | 10
        1 || "auto" | 10
        2 || "scode" | 9
    }

    def "get all participants"() {
        setup:
        def participants = results.participants()

        expect:
        participants.size() == 615
    }

    @Unroll
    def "verify multiple participants #name"() {
        setup:
        def participants = results.participants()

        expect:
        participants[i].country == country
        participants[i].year == year
        participants[i].name == name
        participants[i].score == score

        where:
        i   || country | year | score | name
        0   || "JPN"   | 2015 | 1000  | "Ken Ogura"
        1   || "BGR"   | 2015 | 1000  | "Vasil Sarafov"
        141 || "USA"   | 2016 | 533   | "Kevin Lin"
        613 || "USA"   | 2015 | 0     | "Colby Hanley"
        614 || "IRN"   | 2017 | 0     | "Sabaa Karimi"
    }
}
