/*
 * Module : servicesMonitor-api
 * File   : MessageTracingActor.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package akka

import brave.play.actor.ActorTraceSupport.ActorTraceData
import com.diwo.common.api.BaseService
import com.diwo.common.{BaseActor, ServiceRequest}
import com.typesafe.config.ConfigFactory
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write
import play.api.libs.json.{JsObject, JsValue, Json}
import scalaj.http.{Http, HttpResponse}

trait MessageTracingActor extends BaseActor   {
  val monitorService = ConfigFactory.load().getString("MonitorService")

  override protected[akka] def aroundReceive(receive: Receive, msg: Any): Unit = {
    try {
      logger.info("aroundReceive in "+self.path.name)
      MonitorReceivedMsg(receive, msg)
      super.aroundReceive(receive, msg)
    }
  catch{
    case ex:Exception =>
       logger.debug( ex.getMessage)
  }
  }
  protected def MonitorReceivedMsg(receive: MessageTracingActor.this.Receive, value: Any) {

    try {
      logger.info("in MonitorReceivedMsg")
       if ( value.isInstanceOf[com.diwo.common.ServiceRequest] ){
            var req = value.asInstanceOf[com.diwo.common.ServiceRequest]
         import java.time.Instant
         var unixTimestamp : Long = Instant.now.getEpochSecond

         var t: JsObject = req.requestBody.as[JsObject] ++ Json.obj("Service" -> BaseService.serviceName) ++ Json.obj("ReceivedTS" ->unixTimestamp)

           var newReq = ServiceRequest ( req.requestDirective,t.asInstanceOf[JsValue],req.responseDirective)(ActorTraceData())
            if (! monitorService.isEmpty) {
              logger.info("Sending to "+ monitorService)
              implicit val formats: DefaultFormats = DefaultFormats
              var jsonString=write(newReq)
            //  logger.info("message " + jsonString)
              var response: HttpResponse[String] = Http(monitorService).postData(jsonString.toString).header("content-type", "application/json").asString

              logger.info("Response from Monitoring " + response.toString)
            }else{
              logger.info(" MonitoringService not value " )
            }
      }
    }
    catch {
      case ex:Exception =>
          logger.debug("MonitorReceivedMsg Exception in httprequest" + ex.getMessage)
    }
  }

}
