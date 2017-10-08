package controllers.model

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class InstagramMediaResponse (
  moments: List[InstagramMedia],
  top: List[InstagramMedia]
)

object InstagramMediaResponse {
  implicit val writeJson: Writes[InstagramMediaResponse] = (
    (__ \ "moments").write[List[InstagramMedia]] and
    (__ \ "top").write[List[InstagramMedia]]
  ) (unlift(InstagramMediaResponse.unapply))
}
