package models

/**
  * Created by yxl on 17/1/6.
  */

case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 1)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
  lazy val nextPage = Option(page + 1)
  lazy val prevPage = Option(page - 1)
}