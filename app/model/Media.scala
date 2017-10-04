package model

import java.time.format.DateTimeFormatter
import java.time.{ZoneId, ZonedDateTime}
import java.util.Date

import model.Media.Media
import play.api.libs.functional.syntax._
import play.api.libs.json._


object Media {

  case class Media(
    id: MediaId,
    created: ZonedDateTime,
    lowImage: String,
    stdImage: String,
    tags: List[String]
  )

  val readTimeStamp: Reads[ZonedDateTime] = (json: JsValue) => {
    json.validate[String].map { unixTimestamp =>
      val date = new Date(java.lang.Long.parseLong(unixTimestamp)*1000)
      ZonedDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"))
    }
  }

  val instagramRead: Reads[Media] = (
    (JsPath \ "id").read[MediaId] and
    (JsPath \ "created_time").read[ZonedDateTime](readTimeStamp) and
    (JsPath \ "images" \ "low_resolution" \ "url").read[String] and
    (JsPath \ "images" \ "standard_resolution" \ "url").read[String] and
    (JsPath \ "tags").read[List[String]]
  )(Media.apply _)

}

object MediaWeb {

  case class MediaWeb(
    created: ZonedDateTime,
    lowImage: String,
    stdImage: String,
    tags: List[String]
  )

  def mapMedia(m: Media): MediaWeb =
    MediaWeb(m.created,m.lowImage,m.stdImage,m.tags)

  val mediaWrite: Writes[MediaWeb] = (
    (JsPath \ "created").write[ZonedDateTime](Writes.temporalWrites[ZonedDateTime, DateTimeFormatter](DateTimeFormatter.ISO_OFFSET_DATE_TIME)) and
    (JsPath \ "lowImage").write[String] and
    (JsPath \ "stdImage").write[String] and
    (JsPath \ "tags").write[List[String]]
  )(unlift(MediaWeb.unapply))
}