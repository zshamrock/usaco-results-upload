package by.gsu.dl.usaco.resultsupload

import by.gsu.dl.usaco.resultsupload.domain.Division
import by.gsu.dl.usaco.resultsupload.exception.NoMatchesException
import spock.lang.Specification

class PatternsTest extends Specification {
    def "throw no matches exception"() {
        when:
        Patterns.matchesContest("Final Results: USACO 2014 Contest, Bronze")

        then:
        thrown(NoMatchesException)
    }

    def "matches contest"() {
        when:
        def contest = Patterns.matchesContest("Final Results: USACO 2015 January Contest, Gold")

        then:
        contest.year == 2015
        contest.month == "January"
        contest.division == Division.GOLD
    }
}
