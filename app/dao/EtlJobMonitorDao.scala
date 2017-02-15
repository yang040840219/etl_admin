package dao

import javax.inject.Inject

import models.{EtlJobMonitor, EtlJobMonitorTable}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Created by yxl on 17/1/22.
  */
class EtlJobMonitorDao @Inject()(@NamedDatabase("db_etl") val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val query = TableQuery[EtlJobMonitorTable]

  /**
    * @return 返回负责人列表
    */
  def list():Future[Seq[EtlJobMonitor]] = {
    val action = query.result
    db.run(action)
  }

  /**
    * 根据名称查找
    * @param userName
    * @return
    */
  def get(userName:String):Future[Option[EtlJobMonitor]] = {
     val action = query.filter(_.userName === userName).result.headOption
     db.run(action)
  }

  def saveOrUpdate(etlJobMonitor:EtlJobMonitor):Future[Int] = {
    val action = query.insertOrUpdate(etlJobMonitor)
    db.run(action)
  }

  def delete(id:Int):Future[Int] = {
     val action = query.filter(_.id === id).delete
     db.run(action)
  }

}
