package config

import com.google.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class Api @Inject()(config: Configuration) {

  val imToken: String = config.get[String]("instagram.token")

}
