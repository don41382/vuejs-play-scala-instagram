package model

import play.api.libs.functional.syntax._
import play.api.libs.json._


case class Response(
  nextMaxId : Option[String],
  media: List[Media]
)

object Response {

  implicit val instagramReadsMedia = Media.instagramRead

  val instagramReads: Reads[Response] = (
    (JsPath \ "pagination" \ "next_max_id").readNullable[String] and
      (JsPath \ "data").read[List[Media]]
  )(Response.apply _)

}

object ResponseWeb {

  implicit val media = MediaWeb.mediaWrite

  val mediaWrite: Writes[Response] = (
    (JsPath \ "nextMaxId").writeNullable[String] and
      (JsPath \ "data").write[List[Media]]
    )(unlift(Response.unapply))
}