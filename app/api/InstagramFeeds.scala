package api

import api.model.FeedResponse
import com.google.inject.Inject
import play.api.Logger
import play.api.libs.json.{JsError, JsObject, JsSuccess, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scalaz._
import Scalaz._


object InstagramFeeds {
  sealed trait Error
  case class JsonError(msg: JsObject) extends Error
}

class InstagramFeeds @Inject()(ws: WSClient, cfg: config.Api) {

  import scala.concurrent.ExecutionContext.Implicits.global

  val log = Logger(this.getClass)

  def loadRecentFeeds(nextMaxId: Option[String]): EitherT[Future, InstagramFeeds.Error, FeedResponse] = {
    val params: List[(String,String)] = List(
      ("access_token", cfg.imToken)
    ) ++ nextMaxId.map(id => List(("max_id",id))).getOrElse(List())

    EitherT.fromEither(ws.url(s"https://api.instagram.com/v1/users/self/media/recent")
      .addQueryStringParameters(params:_*)
      .get()
      .map { response =>
        Json.fromJson[FeedResponse](response.json) match {
          case JsSuccess(res,_) =>
            Right(res)
          case e : JsError =>
            Left(InstagramFeeds.JsonError(JsError.toJson(e)))
        }
      })
  }

  def loadAllFeeds(maxPages: Int): EitherT[Future, InstagramFeeds.Error, FeedResponse] = {
    def run(next: Option[String], lresp: Option[FeedResponse], count: Int): EitherT[Future, InstagramFeeds.Error, FeedResponse] = {
      loadRecentFeeds(next).flatMap { res =>
        res.nextMaxId match {
          case None =>
            EitherT.right(Future.successful(merge(res,lresp)))
          case Some(next) =>
            if (count < maxPages) {
              run(Some(next), Some(merge(res,lresp)), count + 1)
            } else {
              EitherT.right(Future.successful(merge(res,lresp)))
            }

        }
      }
    }
    run(None, None, 0)
  }

  def merge(r: FeedResponse, ropt: Option[FeedResponse]): FeedResponse = ropt match {
    case None =>
      r
    case Some(newR) =>
      r.copy(media = newR.media ++ r.media)
  }

}
