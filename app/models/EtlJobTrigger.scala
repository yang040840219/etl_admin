package models

import slick.driver.MySQLDriver.api._

/**
  * Created by yxl on 17/1/22.
  */
case class EtlJobTrigger(id:Option[Int]=None,
                         jobName:String,
                         triggerType:String,
                         startDay:Int,
                         startHour:Int,
                         startMinute:Int)


class EtlJobTriggerTable(tag:Tag) extends Table[EtlJobTrigger](tag,"t_etl_job_trigger") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def jobName = column[String]("job_name")

  def triggerType = column[String]("trigger_type")

  def startDay = column[Int]("start_day")

  def startHour = column[Int]("start_hour")

  def startMinute = column[Int]("start_minute")

  def * = (id.?,jobName,triggerType,startDay,startHour,startMinute) <> ((EtlJobTrigger.apply _).tupled,EtlJobTrigger.unapply)
}