package dao

import javax.inject.Inject

import models.{EtlJobStream, EtlJobStreamTable}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Created by yxl on 17/1/6.
  */
class EtlJobStreamDao @Inject()(@NamedDatabase("db_etl") val dbConfigProvider: DatabaseConfigProvider)
                                  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val query = TableQuery[EtlJobStreamTable]

  /**
    * 保存触发执行Job 关系
    * @param etlJobStream
    * @return
    */
  def saveEtlJobStream(etlJobStream:EtlJobStream): Future[Int] ={
     val action = query returning query.map(_.id) += etlJobStream
     db.run(action)
  }

  /**
    * 查询 触发 jobName 执行 job
    * @param jobName
    * @return
    */
  def findEtlJobStreamByStreamJobName(jobName:String):Future[Option[EtlJobStream]] = {
      val action = query.filter(_.streamJob === jobName).result.headOption
      db.run(action)
  }

  /**
    * 查询 jobName 触发 job 执行
    * @param jobName
    * @return
    */
  def findEtlJobStreamByJobName(jobName:String):Future[Seq[EtlJobStream]] = {
      val action = query.filter(_.jobName === jobName).result
      db.run(action)
  }

  /**
    * 删除 EtlJobStream
    * @param jobName
    * @return
    */
  def deleteEtlJobStreamByJobName(jobName:String):Future[Int] = {
     val action = query.filter(_.streamJob === jobName).delete
     db.run(action)
  }

  /**
    * 更新
    * @param etlJobStream
    * @return
    */
  def updateEtlJobStream(etlJobStream:EtlJobStream):Future[Int] = {
     val action1 = query.filter(_.streamJob === etlJobStream.streamJob).delete
     val action2 = query returning query.map(_.id) += etlJobStream
     val action = action1.andThen(action2).transactionally
     db.run(action)
  }

}
