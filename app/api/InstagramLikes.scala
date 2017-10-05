package api

import java.time.{Instant, ZonedDateTime}
import java.time.temporal.ChronoUnit

import com.google.inject.Inject
import model.{Like, MediaId}
import play.api.Logger
import play.api.cache.AsyncCacheApi
import play.api.libs.json.{JsError, JsSuccess, Json, Reads, _}
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.duration._
import scalaz.Scalaz._
import scalaz.{EitherT, _}


object InstagramLikes {
  sealed trait Error
  case class JsonError(msg: String) extends Error

  val likeReads: Reads[List[Like]] = new Reads[List[Like]] {
    override def reads(json: JsValue): JsResult[List[Like]] =
      (json \ "data").validate[List[Like]](Reads.list[Like](Like.instagramRead))
  }

}

class InstagramLikes @Inject()(ws: WSClient, cfg: config.Api, cache: AsyncCacheApi) {

  import scala.concurrent.ExecutionContext.Implicits.global
  val log = Logger(this.getClass)

  def loadLikes(media: MediaId, created: ZonedDateTime): EitherT[Future, InstagramLikes.Error, List[Like]] = {
    val params: List[(String,String)] = List(
      ("access_token", cfg.imToken)
    )

    EitherT.fromEither(
      cache.getOrElseUpdate(s"like.media.${media.id}", cacheDuration(created))(
        ws.url(s"https://api.instagram.com/v1/media/${media.id}/likes")
        .addQueryStringParameters(params:_*)
        .get()
        .map { response =>
          Json.fromJson(response.json)(InstagramLikes.likeReads) match {
            case JsSuccess(res,_) =>
              Right(res)
            case e : JsError =>
              Left(InstagramLikes.JsonError(JsError.toJson(e).toString))
          }
      }))

  }

  def cacheDuration(created: ZonedDateTime): Duration = {
    val d = ChronoUnit.DAYS.between(created.toInstant, Instant.now())
    if (d <= 1) {
      30 seconds
    } else {
      (1 * (d+1)) hours
    }
  }

}