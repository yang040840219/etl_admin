package util

import play.Play
import play.twirl.api.Html

import scala.io.Source

/**
  * Created by yxl on 17/1/7.
  */
object IncludeHtml {

  val path = Play.application().path().getPath()

  def apply(absolutePath: String): Html = {

    val source = Source.fromFile(path + absolutePath, "UTF-8").getLines().mkString("\n")
    Html(source)
  }

}
