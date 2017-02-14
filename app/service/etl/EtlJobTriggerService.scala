package service.etl

import javax.inject.Inject

import dao.{EtlJobTriggerDao}
import models.EtlJobTrigger

import scala.concurrent.Future

/**
  * Created by yxl on 17/1/22.
  */
class EtlJobTriggerService @Inject()(val etlJobTriggerDao: EtlJobTriggerDao )  {

  def findEtlJobTrigger(jobName:String): Future[Option[EtlJobTrigger]] = {
    etlJobTriggerDao.findEtlJobTrigger(jobName)
  }


  def deleteEtlJobTrigger(jobName:String):Future[Int] = {
    etlJobTriggerDao.deleteEtlJobTrigger(jobName)
  }

}
