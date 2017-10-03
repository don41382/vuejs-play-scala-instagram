package api

import com.google.inject.Inject
import model.{Response, ResponseWeb}
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scalaz._
import Scalaz._


sealed trait LoadRecentFeedsError

case class JsonError(msg: String) extends LoadRecentFeedsError

class Instagram @Inject() (ws: WSClient, cfg: config.Api) {

  import scala.concurrent.ExecutionContext.Implicits.global

  val log = Logger(this.getClass)

  def loadRecentFeeds(nextMaxId: Option[String]): EitherT[Future, LoadRecentFeedsError, Response] = {
    val params: List[(String,String)] = List(
      ("access_token", cfg.imToken)
    ) ++ nextMaxId.map(id => List(("max_id",id))).getOrElse(List())

    EitherT.fromEither(ws.url(s"https://api.instagram.com/v1/users/self/media/recent")
      .addQueryStringParameters(params:_*)
      .get()
      .map { response =>
        Json.fromJson(response.json)(Response.instagramReads) match {
          case JsSuccess(res,_) =>
            Right(res)
          case e : JsError =>
            Left(JsonError(JsError.toJson(e).toString))
        }
      })
  }

  def loadAllFeeds(maxPages: Int): EitherT[Future, LoadRecentFeedsError, Response] = {
    def run(next: Option[String], lresp: Option[Response], count: Int): EitherT[Future, LoadRecentFeedsError, Response] = {
      loadRecentFeeds(next).flatMap { res =>
        res.nextMaxId match {
          case None =>
            log.info(s"requesting done")
            EitherT.right(Future.successful(merge(res,lresp)))
          case Some(next) =>
            log.info(s"requesting next: $next")
            if (count < maxPages) {
              run(Some(next), Some(merge(res,lresp)), count + 1)
            } else {
              EitherT.right(Future.successful(merge(res,lresp)))
            }

        }
      }
    }
    log.info(s"initial request for $maxPages")
    run(None, None, 0)
  }

  def merge(r: Response, ropt: Option[Response]): Response = ropt match {
    case None =>
      r
    case Some(newR) =>
      r.copy(media = newR.media ++ r.media)
  }

}
