package api.model

import java.time.{ZoneId, ZonedDateTime}
import java.util.Date

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Media(
  id: MediaId,
  created: ZonedDateTime,
  lowImage: String,
  stdImage: String,
  tags: List[String]
)

object Media {

  val readTimeStamp: Reads[ZonedDateTime] = (json: JsValue) => {
    json.validate[String].map { unixTimestamp =>
      val date = new Date(java.lang.Long.parseLong(unixTimestamp)*1000)
      ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"))
    }
  }

  implicit val instagramRead: Reads[Media] = (
    (JsPath \ "id").read[MediaId] and
    (JsPath \ "created_time").read[ZonedDateTime](readTimeStamp) and
    (JsPath \ "images" \ "low_resolution" \ "url").read[String] and
    (JsPath \ "images" \ "standard_resolution" \ "url").read[String] and
    (JsPath \ "tags").read[List[String]]
  )(Media.apply _)

}