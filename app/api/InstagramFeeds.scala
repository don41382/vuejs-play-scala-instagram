package api

import com.google.inject.Inject
import model.{MediaId, MediaResponse, MediaResponseWeb}
import play.api.Logger
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scalaz._
import Scalaz._


object InstagramFeeds {
  sealed trait Error
  case class JsonError(msg: String) extends Error
}

class InstagramFeeds @Inject()(ws: WSClient, cfg: config.Api) {

  import scala.concurrent.ExecutionContext.Implicits.global

  val log = Logger(this.getClass)

  def loadRecentFeeds(nextMaxId: Option[String]): EitherT[Future, InstagramFeeds.Error, MediaResponse] = {
    val params: List[(String,String)] = List(
      ("access_token", cfg.imToken)
    ) ++ nextMaxId.map(id => List(("max_id",id))).getOrElse(List())

    EitherT.fromEither(ws.url(s"https://api.instagram.com/v1/users/self/media/recent")
      .addQueryStringParameters(params:_*)
      .get()
      .map { response =>
        Json.fromJson(response.json)(MediaResponse.instagramReads) match {
          case JsSuccess(res,_) =>
            Right(res)
          case e : JsError =>
            Left(InstagramFeeds.JsonError(JsError.toJson(e).toString))
        }
      })
  }

  def loadAllFeeds(maxPages: Int): EitherT[Future, InstagramFeeds.Error, MediaResponse] = {
    def run(next: Option[String], lresp: Option[MediaResponse], count: Int): EitherT[Future, InstagramFeeds.Error, MediaResponse] = {
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

  def merge(r: MediaResponse, ropt: Option[MediaResponse]): MediaResponse = ropt match {
    case None =>
      r
    case Some(newR) =>
      r.copy(media = newR.media ++ r.media)
  }

}
