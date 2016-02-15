package playjsontime

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import play.api.data.validation.ValidationError
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

object JodaIsoDateTimeFormat {

  val dtf: DateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC()
  val validationError = ValidationError("error.expected.joda.datetime.iso.format")

  implicit def dateTimeWrites: Writes[DateTime] = new Writes[DateTime] {
    override def writes(o: DateTime): JsValue = JsString(o.toString(dtf))
  }

  implicit def dateTimeReads: Reads[DateTime] = new Reads[DateTime] {
    override def reads(json: JsValue): JsResult[DateTime] = {
      json.validate[String] flatMap (dateString =>
        Try {
          dtf.parseDateTime(dateString)
        } match {
          case Success(s) => JsSuccess(s)
          case Failure(e) => JsError(JsPath(), validationError)
        })
    }
  }

}
