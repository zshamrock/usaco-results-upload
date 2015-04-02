package by.gsu.dl.usaco.resultsupload.trace

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

import spock.lang.Specification

class InMemoryTraceSpec extends Specification {
    def "add N messages and remove N messages"() {
        given:
        def trace = new InMemoryTrace()
        def messages = ["abc", "def", "ghi", "xyz"]

        when:
        messages.each { trace.add(it) }

        then:
        def latest = trace.latest(messages.size())
        latest.size() == messages.size()
        latest == messages
        trace.latest(1).isEmpty()
    }

    def "add N messages and remove N+1 messages"() {
        given:
        def trace = new InMemoryTrace()
        def messages = ["abc", "def", "ghi", "xyz"]

        when:
        messages.each { trace.add(it) }

        then:
        def latest = trace.latest(messages.size() + 1)
        latest.size() == messages.size()
        latest == messages
        trace.latest(1).isEmpty()
    }

    def "add N messages and remove N-1 messages"() {
        given:
        def trace = new InMemoryTrace()
        def messages = ["abc", "def", "ghi", "xyz"]

        when:
        messages.each { trace.add(it) }

        then:
        def latest = trace.latest(messages.size() - 1)
        latest.size() == messages.size() - 1
        latest == messages.subList(0, messages.size() - 1)
        trace.latest(1) == ["xyz"]
    }

    def "run 10 producer threads, add 5 messages in each producer thread and 2 consumer threads"() {
        given:
        def messages = [].asSynchronized()
        def trace = new InMemoryTrace()
        def producers = []
        def latch = new CountDownLatch(10)
        def stopConsumers = new AtomicBoolean(false)
        10.times { producers << new Thread(new Runnable() {
            @Override
            void run() {
                println("${Thread.currentThread().name} adds 5 messages...")
                trace.add("abc", "def", "ghi", "xyz", "qrs")
                sleep(TimeUnit.SECONDS.toMillis(Math.max(2, (int) it / 2) as long))
                latch.countDown()
            }
        }, "producer-${it}")}

        def consumers = []
        2.times { consumers << new Thread(new Runnable() {
            @Override
            void run() {
                while (!stopConsumers.get()) {
                    println("${Thread.currentThread().name} consumes 3 messages...")
                    messages.addAll(trace.latest(3))
                    sleep(TimeUnit.SECONDS.toMillis(3))
                }
                println("${Thread.currentThread().name} is stopped")
            }
        }, "consumer-${it}")}

        when:
        consumers.each { it.start() }
        producers.each { it.start() }
        latch.await()
        sleep(TimeUnit.SECONDS.toMillis(10))

        print("Reading all the rest...")
        def rest = trace.latest(Trace.LATEST_ALL)
        stopConsumers.set(true)
        sleep(TimeUnit.SECONDS.toMillis(5))

        then:
        (rest + messages).size() == 50
    }
}
