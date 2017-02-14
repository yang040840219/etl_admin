package dao

import javax.inject.Inject

import models.{EtlJobDependencyTable, EtlJobDependency}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
  * Created by yxl on 17/1/6.
  */
class EtlJobDependencyDao @Inject()(@NamedDatabase("db_etl") val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val query = TableQuery[EtlJobDependencyTable]

  /**
    * 保存依赖job 关系
    *
    * @param etlJobDependencySeq
    * @return
    */
  def saveEtlJobDependency(etlJobDependencySeq: Seq[EtlJobDependency]): Future[Seq[Int]] = {
    val action = query returning query.map(_.id) ++= etlJobDependencySeq
    db.run(action)
  }

  /**
    * 删除依赖job 关系
    *
    * @param jobName
    * @return
    */
  def deleteEtlJobDependencyByJobName(jobName:String) : Future[Int]={
    val action = query.filter(_.jobName === jobName).delete
    db.run(action)
  }

  /**
    * 删除依赖Job
    * @param etlJobDependencySeq
    * @return
    */
  def updateEtlJobDependency(etlJobDependencySeq: Seq[EtlJobDependency]):Future[Int] = {
    val action1 = query.filter(_.jobName.inSet(etlJobDependencySeq.map(_.jobName))).delete
    val action2 = query returning query.map(_.id) ++= etlJobDependencySeq
    val action = action1.andThen(action2).transactionally
    db.run(action).map(_.sum) // 合并结果
  }

  /**
    * 获取jobName 的依赖job
    * @param jobName
    * @return
    */
  def findEtlJobDependencyByJobName(jobName:String):Future[Seq[EtlJobDependency]] = {
      val action = query.filter(_.jobName === jobName).result
      db.run(action)
  }
}