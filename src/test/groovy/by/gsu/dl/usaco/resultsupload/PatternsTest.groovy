package by.gsu.dl.usaco.resultsupload

import by.gsu.dl.usaco.resultsupload.exception.NoMatchesException
import spock.lang.Specification

import static by.gsu.dl.usaco.resultsupload.domain.Division.BRONZE
import static by.gsu.dl.usaco.resultsupload.domain.Division.GOLD

class PatternsTest extends Specification {
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

    def "matches contest with division word"() {
        setup:
        def contest = Patterns.matchesContest("Final Results: USACO 2011 November Contest, Bronze Division")

        expect:
        contest.year == 2011
        contest.month == "November"
        contest.division == BRONZE
    }
}
