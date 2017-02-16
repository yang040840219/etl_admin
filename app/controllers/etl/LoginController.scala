package controllers.etl

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

/**
  * Created by yxl on 17/2/15.
  */
class LoginController extends Controller {

  val loginUserForm = Form(
    mapping(
      "userName" -> text,
      "password" -> text
    )(LoginUser.apply)(LoginUser.unapply)
  )

  private def auth(userName:String,password:String):Boolean = {
    if(userName.trim().equals("admin") &&
        password.trim.equals("123456")) {
       true
    }else{
      false
    }
  }

  def login = Action {
    implicit request => {
      loginUserForm.bindFromRequest().fold(
        formWithErrors => {
          println(formWithErrors)
          Ok(views.html.login(""))
        },
        loginUser => {
          val flag = auth(loginUser.userName,loginUser.password)
          flag match {
            case true => Redirect(routes.DashboardController.dashboard()).withSession(
              "Authorized" -> loginUser.userName)
            case false => {
                val message = "登陆失败"
                println(message)
                Ok(views.html.login(message))
            }
          }

        }
      )
    }
  }

  def logout = Action(implicit request => {
      Redirect(routes.LoginController.login()).withNewSession
  })

}
