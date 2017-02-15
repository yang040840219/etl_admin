package controllers.etl

import com.google.inject.Inject
import models.{QueueForm}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import service.etl.EtlJobQueueService
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by yxl on 17/2/14.
  */
class EtlJobQueueController @Inject() (etlJobQueueService:EtlJobQueueService)
  extends Controller {


  val queueForm = Form(
    mapping(
      "jobName" -> optional(text),
      "runDate" -> optional(text),
      "pageNumber" -> optional(number),
      "pageSize" -> optional(number)
    )(QueueForm.apply)(QueueForm.unapply)
  )

  /**
    * 分页显示
    *
    * @return
    */
  def findPage = Action.async(implicit request => {

//    queueForm.bindFromRequest.fold(
//      formWithErrors => {
//        // binding failure, you retrieve the form containing errors:
//        BadRequest(formWithErrors.toString)
//      },
//      queueData => {
//        val futurePages = etlJobQueueService.findPage(queueData.jobName,queueData.runDate,queueData.pageNumber,queueData.pageSize)
//        val page = Await.result(futurePages,Duration.Inf)
//        Ok(views.html.queue.list(page))
//      })

    val body = request.queryString.map({
      case (k,v) => (k,v.head)
    })
    val jobName = body.get("jobName")
    val runDate = body.get("runDate")
    val pageNumber = body.getOrElse("pageNumber","1").toInt
    val pageSize = body.getOrElse("pageSize","50").toInt
    val futurePages = etlJobQueueService.findPage(jobName,runDate,pageNumber,pageSize)
    futurePages.map(page => Ok(views.html.queue.list(page,jobName,runDate)))

  })

  /**
    * 删除
    * @param id
    * @return
    */
  def delete(id:Int) = Action.async(implicit request => {
      val futureRow = etlJobQueueService.delete(id)
      futureRow.map(row => {
        Redirect(routes.EtlJobQueueController.findPage())
      })
  })

}
