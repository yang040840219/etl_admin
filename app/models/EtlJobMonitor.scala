package models
import slick.driver.MySQLDriver.api._

/**
  * Created by yxl on 17/1/22.
  */


case class EtlJobMonitor(id:Option[Int], userName:String, userPhone:String, enable:Int)

class EtlJobMonitorTable(tag:Tag) extends Table[EtlJobMonitor](tag,"t_etl_job_monitor") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def userName = column[String]("user_name")

  def userPhone = column[String]("user_phone")

  def enable = column[Int]("enable")

  def * = (id.?,userName,userPhone,enable) <> ((EtlJobMonitor.apply _).tupled,EtlJobMonitor.unapply)

}


class EtlJobMonitorForm