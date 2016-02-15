package playjsontime

import java.util.TimeZone

import org.joda.time.{DateTime, DateTimeZone, Interval}
import org.specs2.mutable.Specification
import play.api.libs.json.Json

class JodaIntervalFormatTest extends Specification {

  "Joda Interval" should {
    "convert date without timezone to Json with UTC" in {
      val timeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("GMT"))
      val interval = new Interval(DateTime.parse("2015-02-13").toDateTimeISO.withZone(timeZone),
                                  DateTime.parse("2015-02-16").toDateTimeISO.withZone(timeZone))
      val json = Json.toJson(interval)
      Json.stringify(json) must_== "{\"start\":\"2015-02-13T00:00:00.000Z\",\"end\":\"2015-02-16T00:00:00.000Z\"}"
    }

    "convert EST date to Json with UTC" in {
      val timeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("EST"))
      val interval = new Interval(DateTime.parse("2015-02-13").toDateTimeISO.withZone(timeZone),
                                  DateTime.parse("2015-02-16").toDateTimeISO.withZone(timeZone))
      println(interval)
      val json = Json.toJson(interval)
      Json.stringify(json) must_== "{\"start\":\"2015-02-13T00:00:00.000Z\",\"end\":\"2015-02-16T00:00:00.000Z\"}"
    }

    "convert from Json as +0200 datetime" in {
      val json = Json.parse("{\"start\":\"2015-02-13T00:00:00.000+0200\",\"end\":\"2015-02-16T00:00:00.000+0200\"}")
      val interval = json.as[Interval]
      val expected = new Interval(DateTime.parse("2015-02-12T22:00:00.000Z"), DateTime.parse("2015-02-15T22:00:00.000Z"))
      interval.getStart must_== expected.getStart
      interval.getEnd must_== expected.getEnd
    }

    "return a JsError when the json keys are invalid" in {
      val json = Json.parse("""{"foo":1420070400000,"end":1420070400000}""")
      val interval = json.validate[Interval]
      interval.isError must beTrue
      interval.asEither.left.get.head._2.head.message mustEqual "error.expected.jodainterval.format"
    }

    "return a JsError when the json dates are invalid" in {
      val json = Json.parse("""{"start":"moo","end":"goo"}""")
      val interval = json.validate[Interval]
      interval.isError must beTrue
      interval.asEither.left.get.head._2.head.message mustEqual "error.expected.joda.datetime.iso.format"
    }
  }
}
