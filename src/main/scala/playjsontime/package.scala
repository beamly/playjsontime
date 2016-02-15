import play.api.libs.json.Format

package object playjsontime {
  implicit val dateTimeFormat = Format(JodaIsoDateTimeFormat.dateTimeReads, JodaIsoDateTimeFormat.dateTimeWrites)

  implicit val intervalFormat = Format(JodaIntervalFormat.jodaIntervalReads, JodaIntervalFormat.jodaIntervalWrites)
}
