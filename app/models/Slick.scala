package models

import slick.driver.MySQLDriver.api._

/**
  * Created by yxl on 17/2/18.
  */

case class Slick(id:Option[Int] = None,name:String,age:Int)

class SlickTable(tag:Tag) extends Table[(Option[Int],String,Int)](tag,"t_slick") {

  def id = column[Int]("id",O.PrimaryKey,O.AutoInc)

  def name = column[String]("name")

  def age = column[Int]("age")

  def * = (id.?,name,age)

}