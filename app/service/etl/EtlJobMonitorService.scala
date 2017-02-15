package service.etl

import javax.inject.Inject

import dao.EtlJobMonitorDao
import models.EtlJobMonitor

import scala.concurrent.Future

/**
  * Created by yxl on 17/1/22.
  */
class EtlJobMonitorService @Inject()(val etlJobMonitorDao:EtlJobMonitorDao) {

  /**
    * 查询出所有负责人
    * @return
    */
  def list():Future[Seq[EtlJobMonitor]] = {
       etlJobMonitorDao.list()
  }

  /**
    * 根据名称查询负责人
    * @param userName
    * @return
    */
  def get(userName:String): Future[Option[EtlJobMonitor]] = {
        etlJobMonitorDao.get(userName)
  }

  def save(etlJobMonitor:EtlJobMonitor):Future[Int] = {
     etlJobMonitorDao.saveOrUpdate(etlJobMonitor)
  }


  def delete(id:Int): Future[Int] = {
      etlJobMonitorDao.delete(id)
  }

}
