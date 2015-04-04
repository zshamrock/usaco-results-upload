package by.gsu.dl.usaco.resultsupload

import com.google.common.base.Optional

import spock.lang.Specification

import by.gsu.dl.usaco.resultsupload.trace.InMemoryTrace
import by.gsu.dl.usaco.resultsupload.trace.Trace

class HTMLResultsTraceSpec extends Specification {
    def "verify trace messages"() {
        setup:
        def trace = new InMemoryTrace()

        when:
        new HTMLResults(new FileSourceData(getClass().getResource("/feb14_bronze.html").file),
                Optional.of(trace), new Locale("ru", "RU"))

        then:
        def messages = trace.latest(Trace.LATEST_ALL)
        messages.size() == 5
        !messages.any { it.startsWith("[X]") }
        messages.each { println(it) }
    }
}
