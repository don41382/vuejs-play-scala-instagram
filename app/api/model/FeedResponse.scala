package api.model

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class FeedResponse(
  nextMaxId : Option[String],
  media: List[Media]
)

object FeedResponse {

  implicit val instagramReads: Reads[FeedResponse] = (
    (JsPath \ "pagination" \ "next_max_id").readNullable[String] and
      (JsPath \ "data").read[List[Media]]
  )(FeedResponse.apply _)

}