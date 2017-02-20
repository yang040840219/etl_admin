package util

import org.joda.time.format.DateTimeFormat

/**
  * Created by yxl on 17/2/17.
  */
object DateUtil {

  def string2millis(dateTimeString:String):Long = {
    val fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = fmt.parseDateTime(dateTimeString)
    dateTime.getMillis
  }

}
