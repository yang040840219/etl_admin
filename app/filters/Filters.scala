package filters

import javax.inject.Inject

import play.api.http.DefaultHttpFilters

/**
  * Created by yxl on 17/2/15.
  */
class Filters @Inject() (
                          login: LoginFilter
                        ) extends DefaultHttpFilters(login)