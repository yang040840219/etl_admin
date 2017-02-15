package service.etl

import javax.inject.{Singleton, Inject}
import dao.{EtlJobTriggerDao, EtlJobDependencyDao, EtlJobStreamDao, EtlJobDao}
import models._
import org.apache.commons.lang3.StringUtils

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.concurrent._
import ExecutionContext.Implicits.global

/**
  * Created by yxl on 17/1/5.
  */

@Singleton
class EtlJobService @Inject()(val etlJobDao: EtlJobDao,
                              val etlJobStreamDao: EtlJobStreamDao,
                              val etlJobDependencyDao: EtlJobDependencyDao,
                              val etlJobTriggerDao: EtlJobTriggerDao) {

  /**
    * 查询 job
    *
    * @return
    */
  def listEtlJobs(): Seq[EtlJob] = {
    Await.result(etlJobDao.listEtlJobs(), Duration.Inf)
  }

  /**
    * 计算EtlJob 状态数量
    *
    * @return
    */
  def countEtlJobStatus(): Future[Map[String, Int]] = {
    val jobStatus = etlJobDao.countEtlJobStatus()
    jobStatus.map({
      _.toMap[String, Int]
    })
  }


  def findEtlJobById(id:Int):Option[EtlJob] = {
      val futureEtlJob = etlJobDao.findEtlJobById(id)
      val etlJob = Await.result(futureEtlJob,Duration.Inf)
      etlJob
  }

  /**
    * 保存 job
    *
    * @param etlJob
    * @param etlJobStream
    * @param etlJobDependencySeq
    * @return
    */
  def saveEtlJob(etlJob: EtlJob, etlJobStream: EtlJobStream, etlJobDependencySeq: Seq[EtlJobDependency],
                 etlJobTrigger:Option[EtlJobTrigger]): Int = {
    val jobNum = etlJobDao.saveEtlJob(etlJob)
    val streamNum = etlJobStreamDao.saveEtlJobStream(etlJobStream)
    val depNum = etlJobDependencyDao.saveEtlJobDependency(etlJobDependencySeq).map(_.sum)
    val trigger = etlJobTrigger.map(t => {etlJobTriggerDao.saveEtlJobTrigger(t)})
    val triggerNum = trigger match {
      case None => Future(1)
      case Some(future) => future
    }
    Await.result(Future.sequence(List[Future[Int]](jobNum, streamNum, depNum,triggerNum)), Duration.Inf).sum
  }

  def findEtlJobByName(jobName: String): Option[EtlJob] = {
    Await.result(etlJobDao.findEtlJobByName(jobName), Duration.Inf)
  }

  def findEtlJobByNames(jobNames:Seq[String]):Future[Seq[EtlJob]] = {
    etlJobDao.findEtlJobByNames(jobNames)
  }

  def findEtlJobByNameFuture(jobName:String) : Future[Option[EtlJob]] = {
      etlJobDao.findEtlJobByName(jobName)
  }

  /**
    * 修改 EtlJob
    *
    * @param etlJob
    * @param etlJobStream
    * @param etlJobDependencySeq
    * @return
    */
  def updateEtlJob(etlJob: EtlJob, etlJobStream: EtlJobStream,
                   etlJobDependencySeq: Seq[EtlJobDependency],etlJobTrigger:Option[EtlJobTrigger]): Future[Int] = {
    // 更新 etl_job
    val jobNum = etlJobDao.updateEtlJob(etlJob)
    // 更新 stream_job

    val streamNum = etlJobStreamDao.updateEtlJobStream(etlJobStream)
    // 更新 dep_job
    val depNum = etlJobDependencyDao.updateEtlJobDependency(etlJobDependencySeq)

    val trigger = etlJobTrigger.map(etlJobTriggerDao.saveOrUpdateEtlJobTrigger(_))
    val triggerNum = trigger match {
      case None => Future(1)
      case Some(future) => future
    }

    etlJob.jobTrigger match {
      case "dependency" => {
         // 删除job trigger 是 time 的配置
          etlJobTriggerDao.deleteEtlJobTrigger(etlJob.jobName)
        }
      case _ =>
    }

    for {job <- jobNum
         dep <- depNum
         stream <- streamNum
         trigger <- triggerNum
         if job > 0
    } yield {
      job + dep + stream + trigger
    }
  }


  /**
    * 根据名称删除 job
    *
    * @param jobName
    * @return
    */
  def deleteEtlJob(jobName: String): Future[Int] = {
    // 删除 job
    val jobNum = etlJobDao.deleteEtlJobByJobName(jobName)
    // 删除 job 依赖
    val depNum = etlJobDependencyDao.deleteEtlJobDependencyByJobName(jobName)
    // 删除 job 触发
    val streamNum = etlJobStreamDao.deleteEtlJobStreamByJobName(jobName)

    for {job <- jobNum
         dep <- depNum
         stream <- streamNum
         if job > 0
    } yield {
      job + dep + stream
    }
  }

  /**
    * 根据条件分页查找
    *
    * @param jobName
    * @param jobStatus
    * @param pageNumber
    * @param pageSize
    * @return
    */
  def findEtlJobPages(jobName: Option[String], jobStatus: Option[String],
                      pageNumber: Int, pageSize: Int): Future[Page[EtlJob]] = {
    val selectJobName = chargeNone(jobName)
    val selectJobStatus = chargeNone(jobStatus)
    etlJobDao.findEtlJobByFilter(selectJobName, selectJobStatus, pageNumber, pageSize)
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

}
