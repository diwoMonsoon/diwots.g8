/*
 * Module : servicesMonitor-api
 * File   : Messages.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common

import akka.actor.ActorRef
import brave.play.actor.ActorTraceSupport.{ActorTraceData, TraceMessage}
import com.typesafe.config.Config
import play.api.libs.json.{Json, Writes}

import scala.collection.mutable

//import com.github.levkhomich.akka.tracing.{TracingActorLogging, ActorTracing, TracingSupport}


import play.api.libs.json.JsValue

case class enableSimulation()

case class disableSimulation()

case class ServiceCall ( serviceName:String, request: ServiceRequest)


case class ServiceRequest (requestDirective:JsValue, requestBody:JsValue, responseDirective : JsValue  ) (implicit val traceData: ActorTraceData) extends TraceMessage



case class getServiceReference(serviceName:String  )
case class addServiceReference(serviceName:String , serviceReference:String)
case class getServiceState(serviceName:String )
case class changeServiceState(serviceName:String , newState:String)

case class MetaDataConfig (
                                    name:String,
                                    actorName:String,
                                    configName: String,
                                    instanceId: Int )


case class Metadata (name :String, version :String)

case class ForwardToWorker(actorpath :String,msg:Any)
case class  BaseInitialize(configpath:String, links:mutable.HashMap[String, ActorRef])
case class  KnowledgeObjectsReady(koRefs:mutable.HashMap[String, KnowledgeObject])
case class KnowledgeObjectInitialized(requestId:Int, uri:String, requestor:ActorRef)
case class InitializeKnowledgeObjects(knowledgeObjectSpecs:mutable.HashMap[String, Config])
case class PoisonPill()

case class getKO(name:String)


case class loadKO(requestId:Int, spec:Config, ko:KnowledgeObject,Sender:ActorRef)
object ServiceRequest {
  /*def apply(name: String, order: Order): ServiceRequestFilter =
    apply(name, None, None, order)*/

/*  @transient implicit val readValues: Reads[ServiceRequest] = new Reads[ServiceRequest] {
    override def reads(json: JsValue): JsResult[ServiceRequest] = {
      val requestDirective = (json \ "requestDirective").as[JsValue]
      val requestBody = (json \ "requestBody").as[JsValue]
      val responseDirective = (json \ "responseDirective").as[JsValue]

      val traceData = (json \  "traceData").as[ActorTraceData]
      JsSuccess(ServiceRequest(requestDirective, requestBody, responseDirective)(traceData))
    }
  }
  Json.reads[ServiceRequest]*/


  @transient implicit val residentWrites = new Writes[ServiceRequest] {
    def writes(serviceRequest: ServiceRequest):JsValue ={

      Json.obj("requestDirective" -> serviceRequest.requestDirective) ++ Json.obj("requestBody" -> serviceRequest.requestBody) ++ Json.obj("responseDirective" -> serviceRequest.responseDirective)
    }
  }

}
