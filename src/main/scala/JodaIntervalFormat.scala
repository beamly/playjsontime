package jsonplaytime

import org.joda.time.{DateTime, Interval}
import play.api.data.validation.ValidationError
import play.api.libs.json._

object JodaIntervalFormat {
  /**
    * Reads for the `org.joda.time.Interval` type.
    *
    * @param pattern a date pattern, as specified in `java.text.SimpleDateFormat`.
    * @param corrector a simple string transformation function that can be used to transform input String before parsing. Useful when standards are not exactly respected and require a few tweaks
    */
  def jodaIntervalReads(pattern: String, corrector: String => String = identity): Reads[org.joda.time.Interval] = new Reads[org.joda.time.Interval] {

    implicit val dateTimeReads: Reads[DateTime] = Reads.jodaDateReads(pattern, corrector)

    def reads(json: JsValue): JsResult[Interval] = json match {
      case JsObject(m) =>
        val parsedMap = m map {
          case (key, value) if key == "start" || key == "end" =>
            value.validate[DateTime](dateTimeReads) match {
              case JsSuccess(dateTime, path) => JsSuccess(key -> dateTime)
              case JsError(e)                => JsError(e)
            }
          case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jodainterval.format", pattern))))
        }

        val errors = parsedMap.collect {
          case JsError(e) => e
        }.flatten

        if (errors.isEmpty) {
          val dateTimeMap = parsedMap.collect { case JsSuccess(kv@(key, value), path) => key -> value }.toMap
          val start = dateTimeMap.get("start").get
          val end = dateTimeMap.get("end").get
          JsSuccess(new Interval(start, end))
        } else {
          JsError(errors.toSeq)
        }

      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.date"))))
    }
  }

  /**
    * the default implicit Joda Interval reads
    */
  implicit val DefaultJodaIntervalReads = jodaIntervalReads("yyyy-MM-dd")

  /**
    * Serializer for org.joda.time.Interval
    *
    * @param pattern the pattern used by org.joda.time.format.DateTimeFormat
    */
  def jodaIntervalWrites(pattern: String): Writes[org.joda.time.Interval] = new Writes[org.joda.time.Interval] {

    def writes(i: Interval) =
      JsObject(Seq("start" -> JsNumber(i.getStartMillis), "end" -> JsNumber(i.getEndMillis)))
  }

  /**
    * the default implicit Joda Interval writes
    */
  implicit val DefaultJodaIntervalWrites = jodaIntervalWrites("yyyy-MM-dd")


}
