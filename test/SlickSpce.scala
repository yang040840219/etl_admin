import models.SlickTable
import org.scalatest._

import slick.driver.MySQLDriver.api._
import models._
import slick.jdbc.GetResult
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import scala.collection.mutable.Map

/**
  * Created by yxl on 17/2/18.
  */


class SlickSpec extends FlatSpec with Matchers with
  OptionValues with Inside with Inspectors with BeforeAndAfter {


  val db = Database.forConfig("test")

  var slickQuery = TableQuery[SlickTable]

  behavior of "test slick"

  it should "create schema" in {
    db.run(slickQuery.schema.create)
  }

  it should "insert one return id" in {
    //val action = slickQuery.+=((None,"a",1))

    val slick = Slick(None, "a", 1)

    val action = slickQuery.returning(slickQuery.map(_.id)).+=(Slick.unapply(slick).get)
    val futureRow = db.run(action)
    val row = Await.result(futureRow, Duration.Inf)
    println(s"row:$row")
  }


  it should "query return tuple" in {

    val action = slickQuery.result

    val futureResult = db.run(action).map(item => {
      item.map(x => (Slick.apply _).tupled(x)) // tuple -> case class
    })

    val result = Await.result(futureResult, Duration.Inf)

    println(s"result:$result")

  }

  it should "query group by" in {
    val action = slickQuery.groupBy(_.name).map({
      case (name, group) => (name, group.map(_.age).sum)
    }).result
    val futureResult = db.run(action)
    val result = Await.result(futureResult, Duration.Inf)
    println(s"result:$result")
  }

  it should "insert seq with plain sql" in {

    def insert(slick: Slick): DBIO[Int] =
      sqlu"insert into t_slick(name,age) values (${slick.name}, ${slick.age})"

    val inserts = Seq(Slick(None, "a", 1), Slick(None, "b", 2), Slick(None, "c", 3)).map(insert(_))
    val combined: DBIO[Seq[Int]] = DBIO.sequence(inserts)
    val futureResult = db.run(combined)
    val result = Await.result(futureResult, Duration.Inf)
    println(s"result:$result")

  }

  it should "select with plain sql" in {

    implicit val getSlickResult = GetResult(r => Slick(r.nextIntOption(),r.<<,r.nextInt()))

    implicit val getMapResult = GetResult(r => {
      val map = Map[String,Any]()
      val resultSet = r.rs
      val metaData = resultSet.getMetaData
      val columnCount = metaData.getColumnCount
      for(columnIndex <- 1 to columnCount){
        val columnName = metaData.getColumnName(columnIndex)
        val columnType = metaData.getColumnTypeName(columnIndex)
        val slickColumnType = columnType match {
          case "INT" =>  r.nextInt()
          case "TEXT" => r.nextString()
          case _ => None
        }
        map.put(columnName,slickColumnType)
      }
      map
    })

    val table = "t_slick"
    val id = 6
    val sql = sql"""select id,name,age from #$table where id < $id""".as[Map[String,Any]]
    val futureResult = db.run(sql)
    val result = Await.result(futureResult, Duration.Inf)
    println(s"result:$result")

  }

}
