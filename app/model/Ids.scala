package model

import play.api.libs.json._


case class MediaId (id: String)

object MediaId {
  implicit val instagramId: Format[MediaId] = new Format[MediaId] {
    override def reads(json: JsValue): JsResult[MediaId] =
      json.validate[String].map(id => MediaId(id))
    override def writes(o: MediaId): JsValue =
      JsString(o.id)
  }
}