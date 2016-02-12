package playjsontime

import jsonplaytime.JodaIntervalFormat
import org.joda.time.{DateTime, Interval}
import org.specs2.mutable.Specification
import play.api.libs.json.{Json, Reads}

class JodaIntervalFormatTest extends Specification {

  "Joda Interval" should {
    "convert to Json" in {
      val interval = new Interval(DateTime.parse("2015-01-01"), DateTime.parse("2015-01-01"))
      val json = Json.toJson(interval)
      Json.stringify(json) must_== "{\"start\":1420070400000,\"end\":1420070400000}"
    }

    "convert from Json" in {
      val json = Json.parse("""{"start":1420070400000,"end":1420070400000}""")
      val interval = json.as[Interval]
      val expected = new Interval(DateTime.parse("2015-01-01"), DateTime.parse("2015-01-01"))
      interval.getStart must_== expected.getStart
      interval.getEnd must_== expected.getEnd
    }

    "convert from Json with custom date format" in {
      implicit val jodaIntervalReads: Reads[Interval] = JodaIntervalFormat.jodaIntervalReads("dd/MM/yyyy")

      val json = Json.parse("""{"start":"01/01/2015","end":"02/01/2015"}""")
      val interval = json.as[Interval](jodaIntervalReads)
      val expected = new Interval(DateTime.parse("2015-01-01"), DateTime.parse("2015-01-02"))
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
      interval.asEither.left.get.head._2.head.message mustEqual "error.expected.jodadate.format"
    }
  }
}
