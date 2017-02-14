package vo

import models.{EtlJob, EtlJobDependency, EtlJobStream}
import play.api.libs.json.{Writes, Format, Json}

/**
  * Created by yxl on 17/2/3.
  */
object JsonModel {

  implicit val etlJobDetailFormat: Writes[EtlJobDetail] = new Writes[EtlJobDetail] {
    def writes(jobDetail: EtlJobDetail) = Json.obj(
      "jobName" -> jobDetail.jobName,
      "jobDependency" -> jobDetail.jobDependency ,
      "jobStream" -> jobDetail.jobStream
    )
  }
  implicit val jobDependencyFormat:Format[EtlJobDependency] = Json.format[EtlJobDependency]
  implicit val jobStreamFormat:Format[EtlJobStream] = Json.format[EtlJobStream]
  implicit val jobFormat:Format[EtlJob] = Json.format[EtlJob]
}
