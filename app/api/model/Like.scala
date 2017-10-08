package api.model

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Like(
  id: String,
  username: String,
  fullName: String,
  profilePicture: String
)

object Like {

  implicit val jsonReads: Reads[Like] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "username").read[String] and
      (JsPath \ "full_name").read[String] and
      (JsPath \ "profile_picture").read[String]
  ) (Like.apply _)

}
