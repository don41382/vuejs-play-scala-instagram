package controllers

import api.InstagramFeeds
import com.google.inject.Inject
import model.MediaResponseWeb
import play.api.cache.AsyncCacheApi
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.duration._
import scalaz.{-\/, \/-}

class InstagramController @Inject()(cache: AsyncCacheApi, cc: ControllerComponents, api: InstagramFeeds) extends AbstractController(cc){

  import scala.concurrent.ExecutionContext.Implicits.global

  def moments(nextMaxId: Option[String]) = Action.async {
    cache.getOrElseUpdate("allFeeds", 5 minutes)(api.loadAllFeeds(10).run.map(f => f match {
      case \/-(res) =>
        Ok(Json.toJson(MediaResponseWeb.mapResponse(res))(MediaResponseWeb.mediaWrite))
      case -\/(e : InstagramFeeds.JsonError) =>
        BadRequest(e.msg)
    }))
  }

}
