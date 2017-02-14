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


  def list():Future[Seq[EtlJobMonitor]] = {
    val action = query.result
    db.run(action)
  }
}
