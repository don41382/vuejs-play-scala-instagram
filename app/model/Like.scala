package model

/* https://api.instagram.com/v1/media/1608315218450290578_5943688419/likes?access_token= */
case class Like(
  id: String,
  username: String,
  firstName: String,
  profilePicture: String
)

object Like {

}
