package controllers.etl

import javax.inject.Inject

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import play.api.mvc._
import service.etl.EtlJobService
import scala.concurrent.ExecutionContext.Implicits.global

class DashboardController @Inject()(etlJobService:EtlJobService) extends Controller {

  def dashboard = Action.async(implicit request => {

    val jobStatusCount = etlJobService.countEtlJobStatus()

    for{statusCount <- jobStatusCount } yield {
      Ok(views.html.dashboard(statusCount.toMap))
    }

  })


  def jobDurationTime = Action.async(implicit request => {
      val futureDuration = etlJobService.aggLayerDuration("2016-02-12","2016-02-17")
    val objectMapper = new ObjectMapper() with ScalaObjectMapper
    objectMapper.registerModule(DefaultScalaModule)
      futureDuration.map(duration => Ok(objectMapper.writeValueAsString(duration)))
  })

}
