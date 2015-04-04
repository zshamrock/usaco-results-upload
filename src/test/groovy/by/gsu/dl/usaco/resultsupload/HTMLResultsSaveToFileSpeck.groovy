package by.gsu.dl.usaco.resultsupload
import com.google.common.base.Optional

import spock.lang.Specification

import by.gsu.dl.usaco.resultsupload.trace.Trace

class HTMLResultsSaveToFileSpeck extends Specification {
    def "save to file"() {
        setup:
        def bronze = getClass().getResource("/feb14_bronze.html").file
        def results = new HTMLResults(new FileSourceData(bronze), Optional.<Trace> absent(), new Locale("ru", "RU"))
        def saveTo = File.createTempFile(UUID.randomUUID().toString(), "-feb14_bronze.html")

        when:
        results.saveTo(saveTo.absolutePath)

        then:
        def expected = new File(bronze).readLines()*.replaceAll("\\s+", "").join("")
        def actual = saveTo.readLines()*.replaceAll("\\s+", "").join("")
        actual == expected
    }
}
