package service.etl

import javax.inject.Inject

import dao.{EtlJobStreamDao}
import models.EtlJobStream

import scala.concurrent.Future

/**
  * Created by yxl on 17/1/22.
  */
class EtlJobStreamService @Inject()(val etlJobStreamDao: EtlJobStreamDao)  {


  def findEtlJobStreamByStreamJobName(jobName:String):Future[Option[EtlJobStream]] = {
    etlJobStreamDao.findEtlJobStreamByStreamJobName(jobName)
  }

}
