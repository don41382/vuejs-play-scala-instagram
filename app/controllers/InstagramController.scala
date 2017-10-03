package controllers

import api.{Instagram, JsonError}
import com.google.inject.Inject
import model.ResponseWeb
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scalaz.{-\/, \/-}

class InstagramController @Inject()(cc: ControllerComponents, api: Instagram) extends AbstractController(cc){

  import scala.concurrent.ExecutionContext.Implicits.global

  def moments(nextMaxId: Option[String]) = Action.async {
    api.loadAllFeeds(10).run.map(f => f match {
      case \/-(res) =>
        Ok(Json.toJson(res)(ResponseWeb.mediaWrite))
      case -\/(e : JsonError) =>
        BadRequest(e.msg)
    })
  }

}
