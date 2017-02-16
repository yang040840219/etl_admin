package models


import slick.driver.MySQLDriver.api._


/**
  * Created by yxl on 17/1/5.
  */

case class EtlJob(id:Option[Int] = None,jobName:String,jobStatus:String,
                  pendingTime:String,
                  lastStartTime:String,lastEndTime:String,
                  jobScript:String,retryCount:Int,jobTrigger:String,
                  jobPriority: Int,lastRunDate:String,mainMan:String,
                  enable:Int,jobDesc:String)


class EtlJobTable(tag:Tag) extends Table[EtlJob](tag,"t_etl_job"){

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def jobName = column[String]("job_name")
  def jobStatus = column[String]("job_status")
  def pendingTime = column[String]("pending_time")
  def lastStartTime = column[String]("last_start_time")
  def lastEndTime = column[String]("last_end_time")
  def jobScript = column[String]("job_script")
  def retryCount = column[Int]("retry_count")
  def jobTrigger = column[String]("job_trigger")
  def jobPriority = column[Int]("job_priority")
  def lastRunDate = column[String]("last_run_date")
  def mainMan = column[String]("main_man")
  def enable = column[Int]("enable")
  def jobDesc = column[String]("job_desc")

  def * = (id.?,jobName,jobStatus,pendingTime,lastStartTime,
          lastEndTime,jobScript,retryCount,jobTrigger,
          jobPriority,lastRunDate,mainMan,enable,jobDesc) <> ((EtlJob.apply _).tupled,EtlJob.unapply)
}

case class EtlJobForm(jobName:String,
                      jobStatus:Option[String],
                      pendingTime:Option[String],
                      lastStartTime:Option[String],
                      lastEndTime:Option[String],
                      jobScript:String,
                      retryCount:Option[Int],
                      jobTrigger:String,
                      jobPriority: Option[Int],
                      lastRunDate:Option[String],
                      mainMan:String,
                      jobStream:Option[String],
                      jobDependency:Option[String],
                      jobDesc:Option[String],
                      triggerType:Option[String],
                      startDay:Option[Int],
                      startHour:Option[Int],
                      startMinute:Option[Int])