package controllers.etl

import javax.inject.Inject
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.mvc.Controller
import service.etl.EtlJobMonitorService

/**
  * Created by yxl on 17/2/13.
  */
class EtlJobMonitorController @Inject()(etlJobMonitorService:EtlJobMonitorService)
  extends Controller  {

  def list = Action.async( implicit request => {
     val monitors = etlJobMonitorService.list()
     monitors.map(x => Ok(views.html.monitor.list(x)))
  }
  )

}
