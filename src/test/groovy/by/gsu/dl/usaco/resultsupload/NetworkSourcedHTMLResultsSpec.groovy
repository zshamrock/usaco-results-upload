package by.gsu.dl.usaco.resultsupload
import com.google.common.base.Optional

import spock.lang.Narrative
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import by.gsu.dl.usaco.resultsupload.domain.Division
import by.gsu.dl.usaco.resultsupload.domain.Problem
import by.gsu.dl.usaco.resultsupload.exception.HTMLResultsCreationException
import by.gsu.dl.usaco.resultsupload.trace.InMemoryTrace
import by.gsu.dl.usaco.resultsupload.trace.Trace

@Narrative("""
Try to parse all the online published USACO contest results since November, 2011 till February, 2015.
Verify the problems, pre-college participants, observers for each of the contest.

As it fetches results from the internet it might take a while for these features to complete,
so these features are disabled by default. In order to enable you need to pass -Dnetworksourced sys property
to activate it.
""")
class NetworkSourcedHTMLResultsSpec extends Specification {
    @Shared
    private static final Locale LOCALE_RU = new Locale("ru", "RU")

    @Shared
    def MONTHS = ['nov': 'November', 'dec': 'December', 'jan': 'January', 'feb': 'February', 'mar': 'March',
                  'open': 'US Open'].asImmutable()

    def "connect to wrong url"() {
        setup:
        def trace = new InMemoryTrace()
        def source = new NetworkSourceData("http://www.usaco.org/current/data/012013_diamond_results.html",
                Optional.of(trace), LOCALE_RU)

        when:
        new HTMLResults(source, Optional.of(trace), LOCALE_RU)

        then:
        thrown(HTMLResultsCreationException)
        def messages = trace.latest(10)
        messages.size() == 2
        trace.latest(Trace.LATEST_ALL).isEmpty()
    }

    @Requires({sys.networksourced}) // run with -Dnetworksourced to enable
    @Unroll
    def "process #month #year #division"() {
        setup:
        def url = "http://www.usaco.org/current/data/${month}${year}_${division}_results.html"
        def trace = new InMemoryTrace()
        def results = new HTMLResults(new NetworkSourceData(url, Optional.of(trace), LOCALE_RU),
                Optional.of(trace), LOCALE_RU)
        def problems = results.problems()

        expect:
        results.year() == 2000 + year
        results.month() == MONTHS[month]
        results.division() == Division.valueOf(division.toUpperCase())
        problems.size() == expected_problems.size()
        problems.eachWithIndex { problem, index ->
            def expected_problem = expected_problems[index]
            assert problem == new Problem(expected_problem[0], expected_problem[1])
        }
        results.preCollegeParticipants().size() == pre_college
        results.observers().size() == observers

        where:
        month  | year | division || pre_college | observers | expected_problems
        "nov"  | 11   | "bronze" || 820         | 248       | [["ctiming", 10], ["digits", 10], ["moosick", 10], ["pageant", 10]]
        "nov"  | 11   | "silver" || 167         | 34        | [["pageant", 12], ["lineup", 12], ["tilechng", 12]]
        "nov"  | 11   | "gold"   || 98          | 68        | [["median", 10], ["bsudoku", 12], ["steeple", 10]]

        "dec"  | 11   | "bronze" || 597         | 145       | [["haybales", 10], ["photo", 10], ["escape", 10]]
        "dec"  | 11   | "silver" || 175         | 39        | [["photo", 10], ["rblock", 10], ["umbrella", 10]]
        "dec"  | 11   | "gold"   || 80          | 46        | [["photo", 10], ["simplify", 12], ["grassplant", 13]]

        "jan"  | 12   | "bronze" || 434         | 107       | [["gifts", 10], ["stacking", 10], ["grazing", 10]]
        "jan"  | 12   | "silver" || 156         | 31        | [["delivery", 14], ["baleshare", 10], ["climb", 10]]
        "jan"  | 12   | "gold"   || 74          | 45        | [["combos", 10], ["cowrun", 10], ["alliance", 12]]

        "feb"  | 12   | "bronze" || 487         | 112       | [["folding", 10], ["planting", 10], ["moo", 10]]
        "feb"  | 12   | "silver" || 165         | 40        | [["planting", 10], ["cowids", 10], ["relocate", 10]]
        "feb"  | 12   | "gold"   || 101         | 45        | [["coupons", 14], ["symmetry", 15], ["nearcows", 10]]

        "mar"  | 12   | "bronze" || 454         | 94        | [["times17", 10], ["connect", 10], ["wrongdir", 10]]
        "mar"  | 12   | "silver" || 169         | 42        | [["tractor", 10], ["fpot", 10], ["landscape", 10]]
        "mar"  | 12   | "gold"   || 116         | 52        | [["banner", 20], ["restack", 10], ["skyscraper", 12]]

        "open" | 12   | "bronze" || 323         | 46        | [["cowrow", 22], ["3lines", 20], ["islands", 15], ["unlock", 11]]
        "open" | 12   | "silver" || 105         | 17        | [["unlock", 11], ["bookshelf", 10], ["running", 15]]
        "open" | 12   | "gold"   || 96          | 31        | [["tied", 11], ["bookshelf", 18], ["subsets", 21]]

        "nov"  | 12   | "bronze" || 1206        | 328       | [["cowfind", 10], ["typo", 10], ["hshoe", 10]]
        "nov"  | 12   | "silver" || 220         | 76        | [["clumsy", 16], ["distant", 16], ["bbreeds", 16]]
        "nov"  | 12   | "gold"   || 85          | 75        | [["bbreeds", 16], ["cbs", 16], ["btree", 16]]

        "dec"  | 12   | "bronze" || 712         | 147       | [["greetings", 10], ["scramble", 10], ["crazy", 10]]
        "dec"  | 12   | "silver" || 321         | 89        | [["crazy", 10], ["wifi", 10], ["mroute", 10]]
        "dec"  | 12   | "gold"   || 99          | 40        | [["gangs", 12], ["first", 12], ["runaway", 10]]

        "jan"  | 13   | "bronze" || 463         | 76        | [["mirrors", 10], ["paint", 10], ["truth", 10]]
        "jan"  | 13   | "silver" || 236         | 54        | [["paint", 10], ["squares", 10], ["invite", 10]]
        "jan"  | 13   | "gold"   || 132         | 56        | [["lineup", 10], ["island", 11], ["seating", 10]]

        "feb"  | 13   | "bronze" || 561         | 120       | [["relay", 10], ["crossings", 15], ["perimeter", 10]]
        "feb"  | 13   | "silver" || 191         | 40        | [["perimeter", 10], ["tractor", 10], ["msched", 10]]
        "feb"  | 13   | "gold"   || 168         | 41        | [["partition", 17], ["taxi", 12], ["route", 10]]

        "mar"  | 13   | "bronze" || 559         | 122       | [["cowrace", 10], ["proximity", 10], ["assign", 10]]
        "mar"  | 13   | "silver" || 200         | 34        | [["poker", 10], ["painting", 10], ["cowrun", 14]]
        "mar"  | 13   | "gold"   || 188         | 45        | [["cowrun", 14], ["hillwalk", 12], ["necklace", 10]]

        "open" | 13   | "bronze" || 321         | 64        | [["ballet", 10], ["blink", 10], ["photo", 10], ["haywire", 12]]
        "open" | 13   | "silver" || 182         | 27        | [["gravity", 10], ["fuel", 10], ["cruise", 10]]
        "open" | 13   | "gold"   || 154         | 29        | [["photo", 10], ["yinyang", 10], ["eight", 10]]

        "nov"  | 13   | "bronze" || 1247        | 362       | [["combo", 10], ["milktemp", 10], ["nocow", 10]]
        "nov"  | 13   | "silver" || 210         | 68        | [["nocow", 10], ["crowded", 11], ["pogocow", 11]]
        "nov"  | 13   | "gold"   || 119         | 102       | [["empty", 11], ["sight", 11], ["nochange", 13]]

        "dec"  | 13   | "bronze" || 876         | 206       | [["records", 10], ["baseball", 10], ["wormhole", 10]]
        "dec"  | 13   | "silver" || 285         | 68        | [["msched", 11], ["vacation", 10], ["shuffle", 10]]
        "dec"  | 13   | "gold"   || 153         | 59        | [["vacationgold", 10], ["optmilk", 11], ["shufflegold", 10]]

        "jan"  | 14   | "bronze" || 718         | 139       | [["skidesign", 10], ["slowdown", 10], ["bteams", 10]]
        "jan"  | 14   | "silver" || 267         | 62        | [["slowdown", 10], ["ccski", 10], ["recording", 10]]
        "jan"  | 14   | "gold"   || 154         | 59        | [["curling", 10], ["skicourse", 10], ["skilevel", 10]]

        "feb"  | 14   | "bronze" || 615         | 80        | [["mirror", 10], ["auto", 10], ["scode", 9]]
        "feb"  | 14   | "silver" || 313         | 47        | [["auto", 10], ["rblock", 10], ["scode", 10]]
        "feb"  | 14   | "gold"   || 234         | 69        | [["rblock", 10], ["dec", 10], ["boarding", 10]]

        "mar"  | 14   | "bronze" || 517         | 96        | [["reorder", 10], ["lazy", 10], ["cowart", 10]]
        "mar"  | 14   | "silver" || 297         | 42        | [["irrigation", 10], ["lazy", 10], ["mooomoo", 10]]
        "mar"  | 14   | "gold"   || 229         | 70        | [["lazy", 11], ["sabotage", 14], ["fcount", 11]]

        "open" | 14   | "bronze" || 314         | 49        | [["odometer", 10], ["fairphoto", 10], ["decorate", 10]]
        "open" | 14   | "silver" || 210         | 27        | [["fairphoto", 10], ["gpsduel", 10], ["odometer", 10]]
        "open" | 14   | "gold"   || 209         | 41        | [["fairphoto", 10], ["optics", 10], ["code", 10]]

        "dec"  | 14   | "bronze" || 248         | 70        | [["marathon", 15], ["crosswords", 10], ["cowjog", 10], ["learning", 13]]
        "dec"  | 14   | "silver" || 96          | 40        | [["piggyback", 11], ["marathon", 15], ["cowjog", 15]]
        "dec"  | 14   | "gold"   || 205         | 102       | [["guard", 14], ["marathon", 10], ["cowjog", 14]]

        "jan"  | 15   | "bronze" || 348         | 91        | [["cowroute", 12], ["cowroute", 10], ["whatbase", 11], ["meeting", 15]]
        "jan"  | 15   | "silver" || 85          | 23        | [["stampede", 15], ["cowroute", 12], ["meeting", 15]]
        "jan"  | 15   | "gold"   || 231         | 91        | [["cowrect", 14], ["movie", 14], ["grass", 14]]

        "feb"  | 15   | "bronze" || 304         | 73        | [["censor", 15], ["cow", 10], ["hopscotch", 15]]
        "feb"  | 15   | "silver" || 116         | 30        | [["censor", 15], ["hopscotch", 15], ["superbull", 10]]
        "feb"  | 15   | "gold"   || 250         | 71        | [["hopscotch", 15], ["censor", 15], ["fencing", 15]]

        "open" | 15   | "bronze" || 42          | 12        | [["moocrypt", 15], ["geteven", 10], ["trapped", 15], ["palpath", 12]]
        "open" | 15   | "silver" || 54          | 11        | [["bgm", 10], ["trapped", 14], ["buffet", 15]]
        "open" | 15   | "gold"   || 38          | 12        | [["googol", 15], ["palpath", 12], ["trapped", 15]]

        "dec"  | 15   | "bronze"  || 1265       | 343       | [["paint", 10], ["speeding", 10], ["badmilk", 10]]
        "dec"  | 15   | "silver"  || 653        | 227       | [["lightson", 15], ["highcard", 15], ["bcount", 15]]
        "dec"  | 15   | "gold"    || 463        | 205       | [["cardgame", 15], ["feast", 12], ["dream", 16]]
        "dec"  | 15   | "platinum"|| 249        | 114       | [["maxflow", 15], ["cardgame", 15], ["haybales", 10]]
    }
}