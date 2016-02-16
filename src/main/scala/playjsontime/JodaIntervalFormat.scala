package playjsontime

import org.joda.time.{DateTime, Interval}
import play.api.data.validation.ValidationError
import play.api.libs.json._

object JodaIntervalFormat {

  def jodaIntervalReads: Reads[org.joda.time.Interval] = new Reads[org.joda.time.Interval] {

    implicit val dateTimeReads: Reads[DateTime] = JodaIsoDateTimeFormat.dateTimeReads

    def reads(json: JsValue): JsResult[Interval] = json match {
      case JsObject(m) =>
        val parsedMap = m map {
          case (key, value) if key == "start" || key == "end" =>
            value.validate[DateTime](dateTimeReads) match {
              case JsSuccess(dateTime, path) => JsSuccess(key -> dateTime)
              case JsError(e)                => JsError(e)
            }
          case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jodainterval.format"))))
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

  def jodaIntervalWrites: Writes[org.joda.time.Interval] = new Writes[org.joda.time.Interval] {

    implicit val dateTimeWrites: Writes[DateTime] = JodaIsoDateTimeFormat.dateTimeWrites

    def writes(i: Interval) = JsObject(Seq("start" -> Json.toJson(i.getStart), "end" -> Json.toJson(i.getEnd)))
  }

}
