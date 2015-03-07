package by.gsu.dl.usaco.resultsupload

import by.gsu.dl.usaco.resultsupload.domain.Division
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class HTMLResultsSpec extends Specification {
    @Subject
    @Shared
    def results = new HTMLResults(new FileSourceData(getClass().getResource("/feb14_bronze.html").file))

    def "get year, month and division"() {
        expect:
        results.year() == 2014
        results.month() == "February"
        results.division() == Division.BRONZE
    }

    def "verify there are 3 problems"() {
        expect:
        results.problems().size() == 3
    }

    @Unroll
    def "get problem #name"() {
        expect:
        def problems = results.problems()
        problems[i].name == name
        problems[i].testsCount == testsCount

        where:
        i || name     | testsCount
        0 || "mirror" | 10
        1 || "auto"   | 10
        2 || "scode"  | 9
    }

    def "get participants"() {
        expect:
        def participants = results.participants()
        participants.size() == 615
    }
}
