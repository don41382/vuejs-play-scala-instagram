package controllers

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import api.{InstagramFeeds, InstagramLikes}
import com.google.inject.Inject
import controllers.InstagramController.{FeedsLikeError, InstagramMedia}
import play.api.Logger
import play.api.cache.AsyncCacheApi
import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json.{JsPath, Json, Writes}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.Future
import scala.concurrent.duration._
import scalaz.Scalaz._
import scalaz._


class InstagramController @Inject()(cache: AsyncCacheApi, cc: ControllerComponents, feeds: InstagramFeeds, likes: InstagramLikes) extends AbstractController(cc){

  import scala.concurrent.ExecutionContext.Implicits.global

  val log = Logger(this.getClass)

  def moments(nextMaxId: Option[String]) = Action.async {
    cache.getOrElseUpdate("allFeeds", 30 seconds)(momentsWithLikes().run.map(f => f match {
      case \/-(res) =>
        Ok(Json.toJson(res)(Writes.list[InstagramMedia](InstagramController.mediaWrite)))
      case -\/(e : FeedsLikeError) =>
        log.warn(e.toString)
        BadRequest("bad")
    }))
  }

  def momentsWithLikes(): EitherT[Future, FeedsLikeError, List[InstagramMedia]] =
    for {
      resp <- feeds.loadAllFeeds(10).leftMap(FeedsLikeError.feeds)
      lmedias <- resp.media.map(m =>
        likes.loadLikes(m.id,m.created).leftMap(FeedsLikeError.likes).map{ likes =>
          InstagramMedia(
            m.created,
            m.lowImage,
            m.stdImage,
            likes.headOption.map(_.fullName.split(" ").head).getOrElse("be the first liker!"),
            m.tags)
      }).sequenceU
    } yield (lmedias)

}

object InstagramController {

  case class InstagramMedia(
   created: ZonedDateTime,
   lowImage: String,
   stdImage: String,
   firstLiker: String,
   tags: List[String])

  val mediaWrite: Writes[InstagramMedia] = (
    (JsPath \ "created").write[ZonedDateTime](Writes.temporalWrites[ZonedDateTime, DateTimeFormatter](DateTimeFormatter.ISO_OFFSET_DATE_TIME)) and
      (JsPath \ "lowImage").write[String] and
      (JsPath \ "stdImage").write[String] and
      (JsPath \ "first_liker").write[String] and
      (JsPath \ "tags").write[List[String]]
    )(unlift(InstagramMedia.unapply))

  sealed trait FeedsLikeError
  case class FeedsError(err: InstagramFeeds.Error) extends FeedsLikeError
  case class LikesError(err: InstagramLikes.Error) extends FeedsLikeError

  object FeedsLikeError {
    def feeds(e: InstagramFeeds.Error): FeedsLikeError = FeedsError(e)
    def likes(e: InstagramLikes.Error): FeedsLikeError = LikesError(e)
  }
}