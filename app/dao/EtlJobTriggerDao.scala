package dao

import javax.inject.Inject

import models.{EtlJobTriggerTable, EtlJobTrigger}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by yxl on 17/1/22.
  */
class EtlJobTriggerDao @Inject()(@NamedDatabase("db_etl") val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile]  {

  import driver.api._

  val query = TableQuery[EtlJobTriggerTable]


  def saveEtlJobTrigger(etlJobTrigger:EtlJobTrigger):Future[Int] = {
    val action =  query returning query.map(_.id) += etlJobTrigger
    db.run(action)
  }

  /**
    * 根据jobName 查找
    *
    * @param jobName
    * @return
    */
  def findEtlJobTrigger(jobName:String):Future[Option[EtlJobTrigger]] = {
     val action = query.filter(_.jobName === jobName).result.headOption
     db.run(action)
  }

  /**
    * 删除时间触发依赖
    *
    * @param jobName
    * @return
    */
  def deleteEtlJobTrigger(jobName:String):Future[Int] = {
     val action = query.filter(_.jobName === jobName).delete
     db.run(action)
  }

  def updateEtlJobTrigger(etlJobTrigger:EtlJobTrigger):Future[Int] = {

    val action = query.filter(_.jobName === etlJobTrigger.jobName).map(
      job => { (job.jobName,job.startDay,job.startHour,job.startMinute,job.triggerType)
      }
    ).update(etlJobTrigger.jobName,etlJobTrigger.startDay,etlJobTrigger.startHour,
      etlJobTrigger.startMinute,etlJobTrigger.triggerType)

    db.run(action)

  }

  /**
    * 保存或更新 EtlJobTrigger
 *
    * @param etlJobTrigger
    * @return
    */
  def saveOrUpdateEtlJobTrigger(etlJobTrigger:EtlJobTrigger): Future[Int] = {

     val trigger = Await.result(this.findEtlJobTrigger(etlJobTrigger.jobName),Duration.Inf)

     val code = trigger match {
       case None =>  this.saveEtlJobTrigger(etlJobTrigger)
       case Some(x) => this.updateEtlJobTrigger(x)
     }

     code
  }

}
