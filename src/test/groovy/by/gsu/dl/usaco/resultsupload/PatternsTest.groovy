package by.gsu.dl.usaco.resultsupload

import by.gsu.dl.usaco.resultsupload.exception.NoMatchesException
import spock.lang.Specification

class PatternsTest extends Specification {
    def "throw no matches exception"() {
        when:
        Patterns.matchesContest("Final Results: USACO 2014 Contest, Bronze");

        then:
        thrown(NoMatchesException)
    }
}
