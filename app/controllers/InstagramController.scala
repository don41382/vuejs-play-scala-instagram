package controllers

import api.{InstagramFeeds, InstagramLikes}
import com.google.inject.Inject
import controllers.model.InstagramMedia
import play.api.Logger
import play.api.cache.AsyncCacheApi
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.Future
import scala.concurrent.duration._
import scalaz.Scalaz._
import scalaz._
import scala.concurrent.ExecutionContext.Implicits.global

class InstagramController @Inject()(cache: AsyncCacheApi, cc: ControllerComponents, feeds: InstagramFeeds, likes: InstagramLikes) extends AbstractController(cc){

  val log = Logger(this.getClass)

  import InstagramController._

  def moments(nextMaxId: Option[String]) = Action.async {
    cache.getOrElseUpdate("allFeeds", 30 seconds)(momentsWithLikes().run.map(f => f match {
      case \/-(res) =>
        Ok(Json.toJson(res)(Writes.list[InstagramMedia]))
      case -\/(e : FeedsLikeError) =>
        val jsonErr = e match {
          case FeedsError(InstagramFeeds.JsonError(j)) => j
          case LikesError(InstagramLikes.JsonError(j)) => j
        }
        BadRequest(jsonErr)
    }))
  }

  def momentsWithLikes(): EitherT[Future, FeedsLikeError, List[InstagramMedia]] =
    for {
      resp <- feeds.loadAllFeeds(10).leftMap[FeedsLikeError](FeedsError)
      mediaWithLikes <- resp.media.map(m =>
        likes.loadLikes(m.id,m.created).leftMap[FeedsLikeError](LikesError).map{ likes =>
          InstagramMedia(
            m.created,
            m.lowImage,
            m.stdImage,
            likes.take(3).map(l => parseFirstName(l.fullName).getOrElse(l.username)),
            m.tags)
      }).sequenceU
    } yield (mediaWithLikes)

  def parseFirstName(name: String): Option[String] = {
    name.split(" ").toList match {
      case first :: last =>
        if (first.isEmpty) None else Some(first)
      case _ =>
        None
    }
  }
}

object InstagramController {

  sealed trait FeedsLikeError
  case class FeedsError(err: InstagramFeeds.Error) extends FeedsLikeError
  case class LikesError(err: InstagramLikes.Error) extends FeedsLikeError

}