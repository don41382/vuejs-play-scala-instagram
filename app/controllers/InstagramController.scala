package controllers

import java.time.ZonedDateTime

import api.{InstagramFeeds, InstagramLikes}
import com.google.inject.Inject
import controllers.model.{InstagramMedia, InstagramMediaResponse}
import play.api.Logger
import play.api.cache.AsyncCacheApi
import play.api.libs.json.{Json, Writes}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.Future
import scalaz.Scalaz._
import scalaz._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class InstagramController @Inject()(cache: AsyncCacheApi, cc: ControllerComponents, feeds: InstagramFeeds, likes: InstagramLikes) extends AbstractController(cc){

  val log = Logger(this.getClass)

  import InstagramController._

  def moments(nextMaxId: Option[String]) = Action.async {
    val res: EitherT[Future, FeedsLikeError, InstagramMediaResponse] = for {
      moments <- momentsWithLikesCached()
      tops <- topMomentsWithLikesCached(3, ZonedDateTime.now().minusWeeks(1))
    } yield(InstagramMediaResponse(moments,tops))

    res.run.map(f => f match {
      case \/-(res) =>
        Ok(Json.toJson[InstagramMediaResponse](res))
      case -\/(e : FeedsLikeError) =>
        val jsonErr = e match {
          case FeedsError(InstagramFeeds.JsonError(j)) => j
          case LikesError(InstagramLikes.JsonError(j)) => j
        }
        BadRequest(jsonErr)
    })
  }

  def topMomentsWithLikesCached(topCount: Int, beforeDate: ZonedDateTime): EitherT[Future, FeedsLikeError, List[InstagramMedia]] = {
    momentsWithLikesCached().map(_.filter(_.created.isAfter(beforeDate)).groupBy(_.likeCount).toList.sortBy(-_._1).flatMap(_._2).take(topCount))
  }

  def momentsWithLikesCached(): EitherT[Future, FeedsLikeError, List[InstagramMedia]] =
    EitherT.eitherT(cache.getOrElseUpdate("allFeeds", 30 seconds)(momentsWithLikes().run))

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
            m.likeCount,
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