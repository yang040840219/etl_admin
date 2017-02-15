package controllers.etl

import javax.inject.Inject
import play.api.Logger
import models._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import service.etl._
import vo.EtlJobDetail
import vo.JsonModel._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Await}
import play.api.libs.json._

/**
  * Created by yxl on 17/1/6.
  */
class EtlJobController @Inject()(val etlJobService:EtlJobService,
                                 val etlJobMonitorService:EtlJobMonitorService,
                                 val etlJobDependencyService: EtlJobDependencyService,
                                 val etlJobStreamService:EtlJobStreamService,
                                 val etlJobTriggerService:EtlJobTriggerService)
    extends Controller {

  def list = Action( implicit request => {
    val jobs = etlJobService.listEtlJobs()

    val futureDep = etlJobDependencyService.findEtlJobDependencyByJobName("stg_1_3")

    val dep = Await.result(futureDep,Duration.Inf)

    println(dep)

    Ok("123")
  }
  )

  val etlJobForm = Form(
    mapping(
      "jobName" -> text,
      "jobStatus" -> optional(text),
      "pendingTime" -> optional(text),
      "lastStartTime" -> optional(text),
      "lastEndTime" -> optional(text),
      "jobScript" -> text,
      "retryCount" -> optional(number),
      "jobTrigger" -> text,
      "jobPriority" -> optional(number),
      "lastRunDate" -> optional(text),
      "mainMan" -> text,
      "jobStream" -> optional(text),
      "jobDependency" -> optional(text),
      "jobDesc" -> optional(text),
      "triggerType" -> optional(text),
      "startDay" -> optional(number),
      "startHour" -> optional(number),
      "startMinute" -> optional(number)
    )(EtlJobForm.apply)(EtlJobForm.unapply)
  )

  /**
    * 添加前查询信息
    * 返回两个future数据
    * @return
    */
  def prevAdd = Action.async(implicit request => {
    val futureEtlJobMonitorSeq = etlJobMonitorService.list()
    val futureEtlJobs = Future {
      etlJobService.listEtlJobs()
    }
    for{
       etlJobMonitorSeq <- futureEtlJobMonitorSeq
       etlJobs <- futureEtlJobs
    } yield {
      Ok(views.html.job.add(etlJobMonitorSeq,etlJobs))
    }
  })


  /**
    * 添加 Job
    * @return
    */
  def add = Action(implicit request => {
    etlJobForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(formWithErrors.toString)
      },
      etlJobData => {
        Logger.info(s"添加EtlJob:$etlJobData")
        /* binding success, you get the actual value. */
        val etlJob = EtlJob(jobName=etlJobData.jobName,
                            jobStatus = EtlJob.DONE,
                            pendingTime = "",
                            lastStartTime = "",
                            lastEndTime = "",
                            jobScript = etlJobData.jobScript,
                            retryCount =  1,
                            jobTrigger = etlJobData.jobTrigger,
                            jobPriority = 1,
                            lastRunDate = "",
                            mainMan = etlJobData.mainMan,
                            enable = EtlJob.ENABLE,
                            jobDesc = etlJobData.jobDesc.getOrElse(""))
        val jobStream = etlJobData.jobStream.getOrElse("")
        val etlJobStream = EtlJobStream(jobName = jobStream,streamJob = etlJobData.jobName,enable = EtlJobStream.ENABLE)
        val jobDependency = etlJobData.jobDependency
        val etlJobDependencySeq:Seq[EtlJobDependency] = jobDependency match {
          case None => Seq[EtlJobDependency]()
          case Some(jobDeps) => {
                val deps = jobDeps.split(",").map(_.trim()).map(dep => {
                  EtlJobDependency(jobName = etlJobData.jobName,dependencyJob = dep,enable = EtlJob.ENABLE)
                })
                deps
          }
        }
        val jobTriggerType = etlJobData.jobTrigger
        val jobTrigger = jobTriggerType match {
          case "time" => {
              Some(EtlJobTrigger(jobName = etlJobData.jobName,
                                triggerType = etlJobData.triggerType.getOrElse("time"),
                                startDay = etlJobData.startDay.getOrElse(0),
                                startHour = etlJobData.startHour.getOrElse(0),
                                startMinute = etlJobData.startMinute.getOrElse(0)
                                ))
          }
          case _ => None
        }
        etlJobService.saveEtlJob(etlJob,etlJobStream,etlJobDependencySeq,jobTrigger)
        Redirect(routes.EtlJobController.findPage())
      }
    )
  })

  /**
    * 修改前查询
    * @param jobName
    * @return
    */
  def prevUpdate(jobName:String) =Action.async(implicit request => {
    Logger.info(s"prevUpdate:$jobName|")
    val futureEtlJobMonitorSeq = etlJobMonitorService.list() //
    val futureEtlJobs = Future {etlJobService.listEtlJobs()} //
    val futureEtlJobDependency = etlJobDependencyService.findEtlJobDependencyByJobName(jobName)
    val futureEtlJobStream = etlJobStreamService.findEtlJobStreamByStreamJobName(jobName)
    val futureEtlJob = Future {etlJobService.findEtlJobByName(jobName) }
    val futureEtlJobTrigger = etlJobTriggerService.findEtlJobTrigger(jobName)
    for{
      etlJobMonitorSeq <- futureEtlJobMonitorSeq
      etlJobs <- futureEtlJobs
      etlJob <- futureEtlJob
      jobDependency <- futureEtlJobDependency
      jobStream <- futureEtlJobStream
      jobTrigger <- futureEtlJobTrigger
    } yield {
      Ok(views.html.job.update(etlJobMonitorSeq,etlJobs,etlJob,jobDependency,jobStream,jobTrigger))
    }
  })

  /**
    * 更新Job信息
    * @return
    */
  def update = Action({ implicit request =>
    etlJobForm.bindFromRequest.fold(
      formWithErrors => {
        // binding failure, you retrieve the form containing errors:
        BadRequest(formWithErrors.toString)
      },
      etlJobData => {
        Logger.info(s"修改EtlJob:$etlJobData")
        /* binding success, you get the actual value. */
        val etlJob = EtlJob(jobName=etlJobData.jobName,
          jobStatus = EtlJob.DONE,
          pendingTime = etlJobData.pendingTime.getOrElse(""),
          lastStartTime = etlJobData.lastStartTime.getOrElse(""),
          lastEndTime = etlJobData.lastEndTime.getOrElse(""),
          jobScript = etlJobData.jobScript,
          retryCount = etlJobData.retryCount.getOrElse(1),
          jobTrigger = etlJobData.jobTrigger,
          jobPriority = etlJobData.jobPriority.getOrElse(1),
          lastRunDate = etlJobData.lastRunDate.getOrElse(""),
          mainMan = etlJobData.mainMan,
          enable = EtlJob.ENABLE,
          jobDesc = etlJobData.jobDesc.getOrElse(""))
        val jobStream = etlJobData.jobStream.getOrElse("")
        val etlJobStream = EtlJobStream(jobName = jobStream, streamJob = etlJobData.jobName, enable = EtlJobStream.ENABLE)
        val jobDependency = etlJobData.jobDependency
        val etlJobDependencySeq: Seq[EtlJobDependency] = jobDependency match {
          case None => Seq[EtlJobDependency]()
          case Some(jobDependency) => {
            val deps = jobDependency.split(",").map(_.trim()).map(dep => {
              EtlJobDependency(jobName = etlJobData.jobName, dependencyJob = dep, enable = EtlJob.ENABLE)
            })
            deps
          }
        }
        val jobTriggerType = etlJobData.jobTrigger
        val jobTrigger = jobTriggerType match {
          case "time" => {
            Some(EtlJobTrigger(jobName = etlJobData.jobName,
              triggerType = etlJobData.triggerType.getOrElse("time"),
              startDay = etlJobData.startDay.getOrElse(0),
              startHour = etlJobData.startHour.getOrElse(0),
              startMinute = etlJobData.startMinute.getOrElse(0)
            ))
          }
          case _ => None
        }
        etlJobService.updateEtlJob(etlJob, etlJobStream, etlJobDependencySeq,jobTrigger)
        Redirect(routes.EtlJobController.findPage())
      }
    )
  })

  /**
    * 根据Job名称删除Job
    * @param id
    * @return
    */
  def delete(id:Int) = Action(implicit request => {
    Logger.info(s"删除EtlJob:$id")
    val etlJob = etlJobService.findEtlJobById(id)
    etlJob match {
      case Some(job) => {
        val jobName = job.jobName
        etlJobService.deleteEtlJob(jobName)
        Redirect(routes.EtlJobController.findPage())
      }
      case None => {
        Redirect(routes.EtlJobController.findPage())
      }
    }
  })


  /**
    * 分页显示Job信息
    * @return
    */
  def findPage = Action.async(implicit request => {
        val body = request.queryString.map({
          case (k,v) => (k,v.head)
        })
        val jobName = body.get("jobName")
        val jobStatus = body.get("jobStatus")
        val pageNumber = body.getOrElse("pageNumber","1").toInt
        val pageSize = body.getOrElse("pageSize","10").toInt
        val futurePage = etlJobService.findEtlJobPages(jobName,jobStatus,pageNumber,pageSize)
        futurePage.map( page => { Ok(views.html.job.list(page,jobName,jobStatus))})
  })

  /**
    * 查看显示依赖job
    * @param jobName
    * @return
    */
  def showEtlJobMap(jobName:String) = Action(implicit request => {
       val etlJob = etlJobService.findEtlJobByName(jobName)
       etlJob match {
         case None => Ok(Json.toJson("error"))
         case Some(job) =>  {
           val jobName = job.jobName
           val etlJobDependency = Await.result(etlJobDependencyService.findEtlJobDependencyByJobName(jobName),Duration.Inf)
           val etlJobStream = Await.result(etlJobStreamService.findEtlJobStreamByStreamJobName(jobName),Duration.Inf)
           val streamEtlJob = etlJobStream match {
             case Some(e) => etlJobService.findEtlJobByName(e.jobName)
             case _ =>  None
           }
           var dependencyEtlJob = Seq.empty[EtlJob]
           if(! etlJobDependency.isEmpty){
               val futureDependencyEtlJob = etlJobService.findEtlJobByNames(etlJobDependency.map(_.dependencyJob))
               dependencyEtlJob =  Await.result(futureDependencyEtlJob,Duration.Inf)
           }

           val etlJobDetail = EtlJobDetail(jobName,dependencyEtlJob,streamEtlJob)
           Logger.info(Json.prettyPrint(Json.toJson(etlJobDetail)))
           Ok(Json.toJson(etlJobDetail))
         }
       }
  })

}
