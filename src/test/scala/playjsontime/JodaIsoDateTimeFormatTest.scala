package playjsontime

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{ DateTime, DateTimeZone }
import org.specs2.mutable.Specification
import org.specs2.specification.core.Fragments
import play.api.libs.json._

class JodaIsoDateTimeFormatTest extends Specification {

  val expectedFormat = ISODateTimeFormat.dateTime().withZoneUTC()

  val testTimes =
    List(
      "2015-03-23T17:40:42.508Z" -> "2015-03-23T17:40:42.508Z",
      "2015-03-23T18:40:42.508+01:00" -> "2015-03-23T17:40:42.508Z",
      "2015-03-23T17:40:42Z" -> "2015-03-23T17:40:42.000Z",
      "2015-03-23T18:40:42+01:00" -> "2015-03-23T17:40:42.000Z")

  "JodaIsoDateTimeFormat" should {
    Fragments.foreach(testTimes) {
      case (input, expected) =>
        "parse time string " + input in {

          val parsedExpected = expectedFormat.parseDateTime(expected)
          val parsed = Json.parse(s""""$input"""").as[DateTime]

          parsed mustEqual parsedExpected
          parsed.getZone mustEqual (DateTimeZone.UTC)

          Json.stringify(Json.toJson(parsed)) mustEqual ("\"" + expected + "\"")
        }
    }

    "parse a datetime" in {
      val jsonString = """{"startTime": "2015-03-23"}"""

      Json.parse(jsonString).validate[TestCase] mustEqual JsError(__ \ "startTime", JodaIsoDateTimeFormat.validationError)

    }
  }
}

case class TestCase(startTime: DateTime)

object TestCase {
  implicit val format: Format[TestCase] = Json.format[TestCase]
}
