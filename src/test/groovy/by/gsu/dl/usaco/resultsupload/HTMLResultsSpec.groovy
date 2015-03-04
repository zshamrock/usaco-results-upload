package by.gsu.dl.usaco.resultsupload

import by.gsu.dl.usaco.resultsupload.domain.Division
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

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
}
