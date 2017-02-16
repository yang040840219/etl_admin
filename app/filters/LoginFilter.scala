package filters


import javax.inject.{Inject, Singleton}

import akka.stream.Materializer
import play.api.{Logger}
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LoginFilter @Inject()(implicit override val mat: Materializer,
                            exec: ExecutionContext) extends Filter  {

  val log = Logger(this.getClass)

  override def apply(nextFilter: RequestHeader => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {

    val auth = requestHeader.session.get("Authorized")

    val invalidResult = Future.successful(
      Ok(views.html.login("未登录"))
    )

    val uri = requestHeader.uri

    if (auth.isEmpty) {
      if(uri.startsWith("/login")){
        nextFilter(requestHeader)
      }else{
        if(!FilterUtil.shouldFilter(uri)){
          nextFilter(requestHeader)
        }else{
          invalidResult
        }
      }
    }
    else {
      nextFilter(requestHeader)
    }
  }
}

