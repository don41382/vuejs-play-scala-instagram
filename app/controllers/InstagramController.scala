package controllers

import com.google.inject.Inject
import model.{Response, ResponseWeb}
import play.api.Configuration
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}

class InstagramController @Inject()(cc: ControllerComponents, config: Configuration, ws: WSClient) extends AbstractController(cc){

  import scala.concurrent.ExecutionContext.Implicits.global

  def moments(nextMaxId: Option[String]) = Action.async {
    val params: List[(String,String)] = List(
      ("access_token", config.get[String]("instagram.token"))
    ) ++ nextMaxId.map(id => List(("max_id",id))).getOrElse(List())

    ws.url(s"https://api.instagram.com/v1/users/self/media/recent")
      .addQueryStringParameters(params:_*)
      .get()
      .map { response =>
        Json.fromJson(response.json)(Response.instagramReads) match {
          case JsSuccess(res,_) =>
            Ok(Json.toJson(res)(ResponseWeb.mediaWrite))
          case e : JsError =>
            BadRequest(JsError.toJson(e))
        }

      }
  }

}
