package playjsontime

import org.joda.time.DateTime
import org.joda.time.format.{ DateTimeFormatter, DateTimeFormatterBuilder, ISODateTimeFormat }
import play.api.data.validation.ValidationError
import play.api.libs.json._

import scala.util.{ Failure, Success, Try }

object JodaIsoDateTimeFormat {

  val validationError = ValidationError("error.expected.joda.datetime.iso.format")

  private val dtfWithMillis: DateTimeFormatter = ISODateTimeFormat.dateTime()
  private val dtfNoMillis: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()
  val defaultDateTimeFormatter: DateTimeFormatter =
    new DateTimeFormatterBuilder()
      .append(
        dtfWithMillis.getPrinter,
        Array(dtfWithMillis.getParser, dtfNoMillis.getParser))
      .toFormatter
      .withZoneUTC()

  val defaultFormat: Format[DateTime] = format(defaultDateTimeFormatter)


  def writes(dtf: DateTimeFormatter): Writes[DateTime] = new Writes[DateTime] {
    override def writes(o: DateTime): JsValue = JsString(o.toString(dtf))
  }

  def reads(dtf: DateTimeFormatter): Reads[DateTime] = new Reads[DateTime] {
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

  def format(dtf: DateTimeFormatter) = Format(reads(dtf), writes(dtf))

}
