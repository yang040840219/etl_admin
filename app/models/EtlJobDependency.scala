package models

import slick.driver.MySQLDriver.api._

/**
  * Created by yxl on 17/1/6.
  */



case class EtlJobDependency(id:Option[Int] = None,jobName:String,dependencyJob:String,enable:Int)


class EtlJobDependencyTable(tag:Tag) extends Table[EtlJobDependency](tag,"t_etl_job_dependency"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def jobName = column[String]("job_name")
  def dependencyJob = column[String]("dependency_job")
  def enable = column[Int]("enable")

  def * = (id.?,jobName,dependencyJob,enable) <> ((EtlJobDependency.apply _).tupled,EtlJobDependency.unapply)
}
