package dao

import javax.inject.{Singleton, Inject}

import models.{Page, EtlJobTable, EtlJob}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yxl on 17/1/5.
  */

@Singleton
class EtlJobDao @Inject()(@NamedDatabase("db_etl") val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val query = TableQuery[EtlJobTable]

  def listEtlJobs(): Future[Seq[EtlJob]] = {
    db.run(query.result)
  }

  def countEtlJobStatus(): Future[Seq[(String, Int)]] = {
    db.run(query.groupBy(_.jobStatus).map({
      case (status, results) => (status -> results.length)
    }).result)
  }

  /**
    * 保存 EtlJob
    *
    * @param etlJob
    * @return
    */
  def saveEtlJob(etlJob: EtlJob): Future[Int] = {
    val action = query returning query.map(_.id) += etlJob
    db.run(action)
  }

  /**
    * 更新EtlJob
    * @param etlJob
    * @return
    */
  def updateEtlJob(etlJob:EtlJob) : Future[Int] = {
    val action = query.filter(_.jobName === etlJob.jobName).map(
      job => {
          (job.jobStatus,job.pendingTime,job.lastStartTime,
          job.lastEndTime,job.jobScript,job.jobTrigger,
          job.retryCount,job.jobPriority,job.lastRunDate,
          job.mainMan,job.jobDesc)
      }
    ).update(etlJob.jobStatus,etlJob.pendingTime,etlJob.lastStartTime,
      etlJob.lastEndTime,etlJob.jobScript,etlJob.jobTrigger,
      etlJob.retryCount,etlJob.jobPriority,etlJob.lastRunDate,
      etlJob.mainMan,etlJob.jobDesc)

    db.run(action)

  }

  /**
    * 根据主键查找
    * @param id
    * @return
    */
  def findEtlJobById(id:Int):Future[Option[EtlJob]] = {
     val action = query.filter(_.id === id).result.headOption
     db.run(action)
  }

  /**
    * 根据jobName 查找
    *
    * @param jobName
    * @return
    */
  def findEtlJobByName(jobName: String): Future[Option[EtlJob]] = {
    db.run(query.filter(_.jobName === jobName).result.headOption)
  }

  /**
    * 根据JobName 查找
    * @param jobNames
    * @return
    */
  def findEtlJobByNames(jobNames:Seq[String]):Future[Seq[EtlJob]] = {

    val action = for(f <- query if f.jobName inSet(jobNames)) yield f

    db.run(action.result)

  }

  /**
    * 删除 EtlJob
    *
    * @param jobName
    * @return
    */
  def deleteEtlJobByJobName(jobName: String): Future[Int] = {
    val etlJob = query.filter(_.jobName === jobName)
    val action = etlJob.delete
    db.run(action)
  }

  /**
    * 根据条件分页查询EtlJob
    * @param jobName
    * @param jobStatus
    * @param pageNumber
    * @param pageSize
    * @return
    */
  def findEtlJobByFilter(jobName: Option[String], jobStatus: Option[String],
                         pageNumber: Int, pageSize: Int): Future[Page[EtlJob]] = {

    var num = pageNumber - 1
    if (pageNumber <= 0) {
      num = 1
    }
    var size = pageSize
    if (size <= 0) {
      size = 10
    }

    val offset = num * size

    val queryAction = query.filter { etlJob =>
      List(
        jobName.map(name => etlJob.jobName.like('%' + name + '%')),
        jobStatus.map(etlJob.jobStatus === _)
      ).collect({ case Some(criteria) => criteria}).reduceLeftOption(_ && _).getOrElse(true:Rep[Boolean])

    }

    def count = db.run(queryAction.length.result)

    for {
      totalRows <- count
      list = queryAction.drop(offset).take(size).result
      result <- db.run(list)
    } yield Page(result, pageNumber, offset, totalRows)

  }



}
