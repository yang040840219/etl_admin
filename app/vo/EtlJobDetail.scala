package vo

import models.{EtlJob}

/**
  * Created by yxl on 17/1/23.
  */
case class EtlJobDetail(jobName:String,
                        jobDependency:Seq[EtlJob],
                        jobStream:Option[EtlJob])
