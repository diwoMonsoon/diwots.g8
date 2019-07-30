
/*
 * Module : servicesMonitor-api
 * File   : KnowledgeManagerAgent.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */


package com.diwo.common

import java.time.Instant

import akka.actor.ActorRef
import brave.play.ZipkinTraceServiceLike
import brave.play.actor.ActorTraceSupport.ActorTraceData
import com.diwo.common.api.BaseService
import com.typesafe.config._
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write
import play.api.libs.json.{JsObject, JsValue, Json}
import scalaj.http.{Http, HttpResponse}

import scala.collection.mutable.HashMap

class KnowledgeManagerAgent ( override implicit val tracer: ZipkinTraceServiceLike)  extends KnowledgeAgent {

  private var koIndex:HashMap[String, KnowledgeObject]=HashMap.empty[String, KnowledgeObject]
  private var requestStateMap:HashMap[Int, Int] =HashMap.empty[Int, Int]
  private var requestId:Int =0
  private var requestResponse:HashMap[Int, HashMap[String, KnowledgeObject]]= HashMap.empty[Int, HashMap[String, KnowledgeObject]]

  private var koSpecs:HashMap[String,Config]=HashMap.empty[String,Config]
  private var defaultLoaderName=""


  context.become(runningState)


  extendedRunningState = {

    case InitializeKnowledgeObjects(koSpecs:HashMap[String, Config])=>{
      requestId += 1
      var responseObjects:HashMap[String, KnowledgeObject]=HashMap.empty[String, KnowledgeObject]
      var koRefs : HashMap[String,KnowledgeObject]= HashMap.empty[String,KnowledgeObject]

      var pendingKoInitializations=0

      for (specKey <- koSpecs.keys){
        var spec:Config= koSpecs(specKey)
        val name = spec.getString("name")
        val uri= URIGenerator.get("object", name)
        //see if the knowledge object already exists
        if (koIndex.contains(uri) ){
          responseObjects (uri) = koIndex(uri)
        } else {
          // create an instance of the knowledge Object
          pendingKoInitializations +=1

/*          if (!spec.hasPath("class"))
              logger.info("Class is not present in this config",spec)

          val classname= spec.getString("class") //class

          val ko:KnowledgeObject =  Class.forName(classname).newInstance.asInstanceOf[KnowledgeObject]

          koIndex (uri)= ko
          responseObjects(uri )= ko
  */
          if(spec.hasPath("metaData")) {
            implicit val formats: DefaultFormats = DefaultFormats
            var unixTimestamp : Long = Instant.now.getEpochSecond
            val monitorService = ConfigFactory.load().getString("KMMService")
            var temp = "{\"serviceName\":\"dataAggregator\", \"configName\":" + spec.getString("metaData")+ ", \"instanceId\":1 }"
            var req =  ServiceRequest(Json.toJson("{\"requestId\":\"12345\", \"requestor\": \"HIM\", \"requestTS\": 1560344948 }"),
                        Json.toJson(Json.parse(temp)),
                        Json.toJson("{\"response\":\"default\" }"))



            var t: JsObject = req.requestBody.as[JsObject] ++ Json.obj("Service" -> BaseService.serviceName) ++ Json.obj("ReceivedTS" ->unixTimestamp)
            var newReq = ServiceRequest ( req.requestDirective,t.asInstanceOf[JsValue],req.responseDirective)(ActorTraceData())

            var jsonString=write(newReq)
            //  logger.info("message " + jsonString)
            var response: HttpResponse[String] = Http(monitorService).postData(jsonString.toString).header("content-type", "application/json").asString


          }

         }

      }
 //   logger.info("Sending to ",sender().path.name," KnowledgeObjectsReady ")
      sender() ! KnowledgeObjectsReady(responseObjects)
    }

    // message received from loader
    case KnowledgeObjectInitialized(requestId, uri, requestor:ActorRef) =>
    // check if, we are all done
 //     logger.info("KnowledgeObjectInitialized  in ",myURI)
          var pending =requestStateMap(requestId) -1
          if(pending > 0){
            requestStateMap(requestId) = pending
          }
          else {
            val koRefs=requestResponse(requestId)
            requestor! KnowledgeObjectsReady(koRefs)
            // drop the request Id from hashmaps
            requestResponse -= requestId
            requestStateMap -= requestId
          }
    case getKO(name:String)=>
      sender ! koIndex(name)
    case _=>
  }


}
