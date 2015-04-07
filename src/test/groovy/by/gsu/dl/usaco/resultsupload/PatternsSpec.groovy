package by.gsu.dl.usaco.resultsupload

import static by.gsu.dl.usaco.resultsupload.domain.Division.BRONZE
import static by.gsu.dl.usaco.resultsupload.domain.Division.GOLD

import spock.lang.Specification

import by.gsu.dl.usaco.resultsupload.exception.NoMatchesException

class PatternsSpec extends Specification {
    def "throw no matches exception"() {
        when:
        Patterns.matchesContest("Final Results: USACO 2014 Contest, Bronze")

        then:
        thrown(NoMatchesException)
    }

    def "matches contest"() {
        setup:
        def contest = Patterns.matchesContest("Final Results: USACO 2015 January Contest, Gold")

        expect:
        contest.year == 2015
        contest.month == "January"
        contest.division == GOLD
    }

    def "matches US Open"() {
        setup:
        def contest = Patterns.matchesContest("Final Results: USACO 2014 US Open, Gold")

        expect:
        contest.year == 2014
        contest.month == "US Open"
        contest.division == GOLD
    }

    def "matches contest with division word"() {
        setup:
        def contest = Patterns.matchesContest("Final Results: USACO 2011 November Contest, Bronze Division")

        expect:
        contest.year == 2011
        contest.month == "November"
        contest.division == BRONZE
    }

    def "matches US Open with division word"() {
        setup:
        def contest = Patterns.matchesContest("Final Results: USACO 2011 US Open, Gold Division")

        expect:
        contest.year == 2011
        contest.month == "US Open"
        contest.division == GOLD
    }

    def "verify matches against allowed participant names"() {
        expect:
        Patterns.matchesParticipantName(name) == matches

        where:
        name                     || matches
        "Pablo Picasso"          || true
        "Александр Пушкин"       || true
        "Big Hero 6"             || true
        "Martin Luther King Jr." || true
        "Harry"                  || true
        "Ralph Waldo-Emerson"    || true
        "Cheshire, cat"          || false
        "淑涵 秦"                 || false
    }
}
