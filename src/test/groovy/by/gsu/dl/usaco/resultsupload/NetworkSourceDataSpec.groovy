package by.gsu.dl.usaco.resultsupload
import org.jsoup.HttpStatusException

import com.google.common.base.Optional

import spock.lang.Specification

import by.gsu.dl.usaco.resultsupload.trace.Trace

class NetworkSourceDataSpec extends Specification {
    def "connect to wrong url"() {
        setup:
        def source = new NetworkSourceData("http://www.usaco.org/current/data/012013_diamond_results.html",
                Optional.<Trace> absent(), Locale.getDefault())

        when:
        source.document()

        then:
        def e = thrown(HttpStatusException)
        e.statusCode == 404
    }
}
