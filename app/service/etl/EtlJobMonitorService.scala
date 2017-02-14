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


}
