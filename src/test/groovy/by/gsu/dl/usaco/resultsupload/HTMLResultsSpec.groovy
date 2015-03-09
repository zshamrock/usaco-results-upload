package by.gsu.dl.usaco.resultsupload

import static by.gsu.dl.usaco.resultsupload.domain.Division.BRONZE

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Subject
import spock.lang.Unroll

import by.gsu.dl.usaco.resultsupload.domain.Submission

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
        problems.size() == 3
        problems[i].name == name
        problems[i].testsCount == testsCount

        where:
        i || name     | testsCount
        0 || "mirror" | 10
        1 || "auto"   | 10
        2 || "scode"  | 9
    }

    def "get all pre-college participants"() {
        setup:
        def participants = results.preCollegeParticipants()

        expect:
        participants.size() == 615
    }

    @Unroll
    def "verify multiple pre-college participants #name"() {
        setup:
        def participants = results.preCollegeParticipants()

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

    @Unroll
    def "verify submissions for multiple pre-college participants #name"() {
        setup:
        def problemsNames = ["mirror", "auto", "scode"]
        def participants = results.preCollegeParticipants()

        expect:
        participants[i].submissions.size() == 3
        participants[i].name == name
        participants[i].submissions.eachWithIndex { submission, index ->
            assert submission == new Submission(problemsNames[index], submissions[index])
        }

        where:
        i   || name            | submissions
        0   || "Ken Ogura"     | ["**********", "**********", "*********"]
        1   || "Vasil Sarafov" | ["**********", "**********", "*********"]
        141 || "Kevin Lin"     | ["*****xxxxx", "*xxxxxxxxx", "*********"]
        484 || "Roman Kachur"  | ["**t*tttttt", "          ", "         "]
        613 || "Colby Hanley"  | ["          ", "xxxxxxxxxx", "         "]
        614 || "Sabaa Karimi"  | ["          ", "xxssssssss", "         "]
    }

    def "get all observers"() {
        setup:
        def observers = results.observers()

        expect:
        observers.size() == 80
    }

    @Unroll
    def "verify multiple observers #name"() {
        setup:
        def observers = results.observers()

        expect:
        observers[i].country == country
        observers[i].year == year
        observers[i].name == name
        observers[i].score == score

        where:
        i   || country | year | score | name
        0   || "NZL"   | 9999 | 1000  | "Bill Rogers"
        1   || "RUS"   | 9999 | 1000  | "Nikita Shapovalov"
        21  || "DEU"   | 9999 | 633   | "Gunnar Birke"
        78  || "MYS"   | 9999 | 0     | "Ting Le Wei"
        79  || "BOL"   | 9999 | 0     | "Mauri Wilde"
    }

    @Unroll
    def "verify submissions for observers #name"() {
        setup:
        def problemsNames = ["mirror", "auto", "scode"]
        def observers = results.observers()

        expect:
        observers[i].submissions.size() == 3
        observers[i].name == name
        observers[i].submissions.eachWithIndex { submission, index ->
            assert submission == new Submission(problemsNames[index], submissions[index])
        }

        where:
        i  || name                | submissions
        0  || "Bill Rogers"       | ["**********", "**********", "*********"]
        1  || "Nikita Shapovalov" | ["**********", "**********", "*********"]
        21 || "Gunnar Birke"      | ["*******s*s", "*xxxxxxxxx", "*********"]
        78 || "Ting Le Wei"       | ["cccccccccc", "          ", "         "]
        79 || "Mauri Wilde"       | ["          ", "tttttttttt", "         "]
    }
}
