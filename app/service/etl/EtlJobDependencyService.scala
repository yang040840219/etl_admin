package service.etl

import javax.inject.Inject

import dao.{EtlJobDependencyDao}
import models.EtlJobDependency

import scala.concurrent.Future

/**
  * Created by yxl on 17/1/22.
  */
class EtlJobDependencyService  @Inject()(val etlJobDependencyDao: EtlJobDependencyDao) {


  def findEtlJobDependencyByJobName(jobName:String):Future[Seq[EtlJobDependency]] = {

    etlJobDependencyDao.findEtlJobDependencyByJobName(jobName)

  }

}
