import org.joda.time.DateTime
import play.api.libs.json.Format

package object playjsontime {

  implicit val dateTimeFormat: Format[DateTime] = JodaIsoDateTimeFormat.defaultFormat

  implicit val intervalFormat = Format(JodaIntervalFormat.jodaIntervalReads, JodaIntervalFormat.jodaIntervalWrites)

}
