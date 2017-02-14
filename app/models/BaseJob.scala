package models


/**
  * Created by yxl on 17/1/5.
  */
trait BaseJob {

  val DONE = "Done"
  val RUNNING = "Running"
  val FAILED = "Failed"
  val PENDING = "Pending"
  val ENABLE = 0
  val NOT_ENABLE = 1

}