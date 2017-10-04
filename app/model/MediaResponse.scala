package model

import model.Media.Media
import model.MediaWeb.MediaWeb
import play.api.libs.functional.syntax._
import play.api.libs.json._


case class MediaResponse(
  nextMaxId : Option[String],
  media: List[Media]
)

object MediaResponse {

  implicit val instagramReadsMedia = Media.instagramRead

  val instagramReads: Reads[MediaResponse] = (
    (JsPath \ "pagination" \ "next_max_id").readNullable[String] and
      (JsPath \ "data").read[List[Media]]
  )(MediaResponse.apply _)

}

object MediaResponseWeb {

  implicit val media = MediaWeb.mediaWrite

  case class MediaWebResponse(
    status: String,
    media: List[MediaWeb]
  )

  def mapResponse(r: MediaResponse): MediaWebResponse =
    MediaWebResponse("ok",r.media.map(MediaWeb.mapMedia))

  val mediaWrite: Writes[MediaWebResponse] = (
    (JsPath \ "status").write[String] and
      (JsPath \ "data").write[List[MediaWeb]]
    )(unlift(MediaWebResponse.unapply))
}