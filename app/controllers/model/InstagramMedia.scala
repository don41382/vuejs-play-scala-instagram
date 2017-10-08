package controllers.model

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class InstagramMedia(
  created: ZonedDateTime,
  lowImage: String,
  stdImage: String,
  firstLiker: List[String],
  likeCount: Int,
  tags: List[String]
)

object InstagramMedia {

  implicit val mediaWrite: Writes[InstagramMedia] = (
    (JsPath \ "created").write[ZonedDateTime](Writes.temporalWrites[ZonedDateTime, DateTimeFormatter](DateTimeFormatter.ISO_OFFSET_DATE_TIME)) and
      (JsPath \ "lowImage").write[String] and
      (JsPath \ "stdImage").write[String] and
      (JsPath \ "first_liker").write[List[String]] and
      (JsPath \ "like_count").write[Int] and
      (JsPath \ "tags").write[List[String]]
    )(unlift(InstagramMedia.unapply))

}