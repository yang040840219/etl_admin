package controllers.etl

import javax.inject.Inject
import models.EtlJobMonitor
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.Controller
import service.etl.EtlJobMonitorService
import vo.JsonModel._
import play.api.data._
import play.api.data.Forms._

import scala.concurrent.duration.Duration

/**
  * Created by yxl on 17/2/13.
  */
class EtlJobMonitorController @Inject()(etlJobMonitorService: EtlJobMonitorService)
  extends Controller {

  def list = Action.async(implicit request => {
    val monitors = etlJobMonitorService.list()
    monitors.map(x => Ok(views.html.monitor.list(x)))
  }
  )


  val monitorForm = Form(
    mapping(
      "id" -> optional(number),
      "userName" -> text,
      "userPhone" -> text,
      "enable" -> number
    )(EtlJobMonitor.apply)(EtlJobMonitor.unapply)
  )


  /**
    * ajax 保存or更新
    *
    * @return
    */
  def ajaxSave = Action(implicit request => {
    monitorForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(formWithErrors.toString)
      },
      monitorData  => {
        val monitor = EtlJobMonitor (monitorData.id, monitorData.userName, monitorData.userPhone, 0)
        val futureRows = etlJobMonitorService.save(monitor)
        val rows = Await.result(futureRows,Duration.Inf)
        Ok(Json.toJson(rows))
      })
  })

  /**
    * ajax 根据名称查找
    *
    * @param userName
    * @return
    */
  def ajaxGet(userName: String) = Action.async(implicit request => {
    val futureMonitor = etlJobMonitorService.get(userName)
    futureMonitor.map(monitor => Ok(Json.toJson(monitor)))
//    futureMonitor.map(monitor => {
//      case Some(m) => Ok(Json.toJson(m))
//      case None => Ok(Json.toJson("no userName"))
//      case _ => Ok(Json.toJson("error"))
//    })
  })

  def delete(id:Int) = Action.async(implicit request => {
      val futureRow = etlJobMonitorService.delete(id)
      futureRow.map(row => Redirect(routes.EtlJobMonitorController.list()))
  })

}
