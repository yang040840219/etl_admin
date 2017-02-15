package models

import slick.driver.MySQLDriver.api._

/**
  * Created by yxl on 17/2/15.
  */
case class EtlJobQueue(id: Int, jobName: String, createTime: String,
                       runTime: String, runNumber: Int, jobStatus: String, runDate: String)

class EtlJobQueueTable(tag: Tag) extends Table[EtlJobQueue](tag, "t_etl_job_queue") {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def jobName = column[String]("job_name")

  def createTime = column[String]("create_time")

  def runTime = column[String]("run_time")

  def runNumber = column[Int]("run_number")

  def jobStatus = column[String]("job_status")

  def runDate = column[String]("run_date")

  def * = (id, jobName, createTime, runTime, runNumber, jobStatus, runDate) <>((EtlJobQueue.apply _).tupled, EtlJobQueue.unapply)

}


case class QueueForm(jobName:Option[String],runDate:Option[String],
                     pageNumber:Option[Int],pageSize:Option[Int])