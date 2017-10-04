package model

import play.api.libs.functional.syntax._
import play.api.libs.json._


/* https://api.instagram.com/v1/media/1608315218450290578_5943688419/likes?access_token= */
case class Like(
  id: String,
  username: String,
  fullName: String,
  profilePicture: String
)

object Like {

  val instagramRead: Reads[Like] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "username").read[String] and
      (JsPath \ "full_name").read[String] and
      (JsPath \ "profile_picture").read[String]
  ) (Like.apply _)

}
