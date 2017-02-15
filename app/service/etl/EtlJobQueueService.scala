package service.etl

import javax.inject.Inject

import dao.EtlJobQueueDao
import models.{Page, EtlJobQueue}
import org.apache.commons.lang3.StringUtils

import scala.concurrent.Future

/**
  * Created by yxl on 17/2/14.
  */
class EtlJobQueueService @Inject()(etlJobQueueDao:EtlJobQueueDao) {

  /**
    * 分页查找
    *
    * @param jobName
    * @param runDate
    * @param pageNumber
    * @param pageSize
    * @return
    */
  def findPage(jobName: Option[String], runDate: Option[String],
               pageNumber: Int, pageSize:Int): Future[Page[EtlJobQueue]] = {

      val selectJobName = chargeNone(jobName)
       val selectRunDate = chargeNone(runDate)

        etlJobQueueDao.findPage(selectJobName,selectRunDate,pageNumber,pageSize)
  }

  def chargeNone(condition:Option[String])  = {
    condition match {
      case Some(value) => {
        if(StringUtils.isEmpty(value)){
          None
        }else{
          condition
        }
      }
      case _ => None
    }
  }

  def delete(id:Int):Future[Int] = {
       etlJobQueueDao.delete(id)
  }

}
