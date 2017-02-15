package dao

import javax.inject.Inject

import models.{Page, EtlJobQueue, EtlJobQueueTable}
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import play.db.NamedDatabase
import slick.driver.JdbcProfile
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yxl on 17/2/15.
  */
class EtlJobQueueDao @Inject()(@NamedDatabase("db_etl") val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val query = TableQuery[EtlJobQueueTable]


  def findPage(jobName: Option[String], runDate: Option[String],
               pageNumber: Int, pageSize: Int): Future[Page[EtlJobQueue]] = {

    var num = pageNumber - 1
    if (pageNumber <= 0) {
      num = 1
    }
    var size = pageSize
    if (size <= 0) {
      size = 50
    }

    val offset = num * size

    val queryAction = query.filter { etlJobQueue =>
      List(
        jobName.map(name => etlJobQueue.jobName.like(name + '%')),
        runDate.map(etlJobQueue.runDate === _)
      ).collect({ case Some(criteria) => criteria }).reduceLeftOption(_ && _).getOrElse(true: Rep[Boolean])

    }

    def count = db.run(queryAction.length.result)

    for {
      totalRows <- count
      list = queryAction.drop(offset).take(size).result
      result <- db.run(list)
    } yield Page(result, pageNumber, offset, totalRows)
  }


  def delete(id:Int):Future[Int] = {
      val action = query.filter(_.id === id).delete
      db.run(action)
  }

}
