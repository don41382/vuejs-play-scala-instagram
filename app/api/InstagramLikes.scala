package api

import java.time.{Instant, ZonedDateTime}
import java.time.temporal.ChronoUnit

import api.model.{Like, MediaId}
import com.google.inject.Inject
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
  case class JsonError(msg: JsObject) extends Error
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
          Json.fromJson[List[Like]](response.json)((__ \ "data").read(Reads.list[Like])) match {
            case JsSuccess(res,_) =>
              Right(res)
            case e : JsError =>
              Left(InstagramLikes.JsonError(JsError.toJson(e)))
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