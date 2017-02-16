package filters


/**
  * Created by yxl on 16/8/26.
  */
object FilterUtil {

  def shouldFilter(uri:String): Boolean ={
    if(uri.startsWith("/assets/lib")){
      false
    }else{
      true
    }
  }

}
