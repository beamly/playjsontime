package playjsontime

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{ DateTime, DateTimeZone }
import org.specs2.mutable.Specification
import play.api.libs.json._

class JsonTest extends Specification {

  val utcString: String = "2015-03-23T17:40:42.508Z"
  val dt = DateTime.parse(utcString, ISODateTimeFormat.dateTime().withOffsetParsed())
  "DateTime parsing" should {

    "parse a datetime" in {

      Json.parse(s""""$utcString"""").as[DateTime] mustEqual (dt)
    }

    "parse a different timezone" in {
      val plainIsoFormat = ISODateTimeFormat.dateTime()

      val differentTimeZone = dt.withZone(DateTimeZone.forOffsetHours(1))

      val differentTimeZoneString = differentTimeZone.toString(plainIsoFormat)
      differentTimeZoneString mustEqual "2015-03-23T18:40:42.508+01:00"

      val jsonString = s"""{"start_time": "${differentTimeZone}"}"""

      Json.parse(s""""$differentTimeZoneString"""").as[DateTime] mustEqual (dt)
      Json.parse(s""""$differentTimeZoneString"""").as[DateTime].getZone mustEqual (DateTimeZone.UTC)
      Json.parse(s""""$differentTimeZoneString"""").as[DateTime] must not be equalTo(differentTimeZone)
    }

    "parse a datetime" in {
      val dt = DateTime.parse(utcString)

      val jsonString = """{"startTime": "2015-03-23"}"""

      Json.parse(jsonString).validate[TestCase] mustEqual JsError((__ \ "startTime"), validationError)

    }
  }
}

case class TestCase(startTime: DateTime)
object TestCase {
  implicit val format: Format[TestCase] = Json.format[TestCase]
}
