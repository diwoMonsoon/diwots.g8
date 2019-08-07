/*
 * Module : servicesMonitor-impl
 * File   : BaseServiceImpl.scala
 * Last Modified : 7/29/19, 9:49 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common.impl

import akka.actor.{ActorSystem, Props}
import brave.play.actor.ActorTraceSupport.{ActorTraceData, TraceableActorRef}
import brave.play.actor.ZipkinTraceService
import com.diwo.common.ServiceRequest
import com.diwo.common.api.BaseService
//import com.diwo.ServicesBase.api.ServicesBaseService
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.transport.ResponseHeader
import com.typesafe.config.ConfigFactory
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable
import scala.concurrent.Future

class BaseServiceImpl  extends BaseService  {

 //val kafkaReg =  system.actorOf(Props(Class.forName("com.diwo.ServicesBase.registry.serviceRegistry")),name ="serviceRegistryActor")
 //kafkaReg ! "start"

  val system = ActorSystem("mySystem")

  implicit val tracer = new ZipkinTraceService(system, "zipkin-akka-actor")
 var serviceReference :mutable.HashMap[String,String ] = mutable.HashMap.empty[String,String]
 var serviceState :mutable.HashMap[String,String ] = mutable.HashMap.empty[String,String]

 var simulateService = false

 simulateService = ConfigFactory.load().getBoolean("SimulateService")
//
 var serviceTopActorName  = ConfigFactory.load().getString("ServiceTopActor")
//
 var serviceTopActor =system.actorOf(Props(Class.forName("com.diwo.common.ServiceTopActor"),tracer),name ="baseServiceTopActor")

  var simulatorActorName  = ConfigFactory.load().getString("SimulatorActorName")


 var mgmtTopActorName  = ConfigFactory.load().getString("MgmtTopActor")

 var mgmtTopActor = system.actorOf(Props(Class.forName(mgmtTopActorName)),name ="mgmtTopActor")

 mgmtTopActor ! "initialize"
 
  val linkRefs: mutable.HashMap[String, ActorRef] = mutable.HashMap.empty[String, ActorRef]
  //
   linkRefs(URIGenerator.get("worker", "knowledge_manager")) = serviceTopActor
  val topAgentConfigPath= "$serviceTopActorName.conf"
  TraceableActorRef(serviceTopActor) ! BaseInitialize(topAgentConfigPath, linkRefs)



 override def hello(id: String) = ServiceCall { _ =>
   val responseHeader = ResponseHeader.Ok.withHeader("status","service response")
   Future.successful("hello " + id )
 }

 def mgmtRequest()= ServiceCall { request =>

   val responseHeader = ResponseHeader.Ok.withHeader("status","service response")

   var requestDirective = Json.toJson(Json.parse(request.toString())).\("requestDirective").as[JsValue]
   var requestBody= Json.toJson(Json.parse(request.toString())).\("requestBody").as[JsValue]
   var responseDirective = Json.toJson(Json.parse(request.toString())).\("responseDirective").as[JsValue]


   var cmdName =   requestBody.\("commandName").as[String]
   cmdName match {
     case "enableSimulation" =>
       simulateService = true
       logger.info("Simulating request " + requestBody.\("serviceName").as[String])
     case "disableSimulation" =>
       simulateService = false
       logger.info("Simulating request " + requestBody.\("serviceName").as[String])
     case _ =>
       mgmtTopActor ! ServiceRequest(requestDirective,requestBody,responseDirective)(ActorTraceData())
   }

   Future.successful( Json.parse("""{"request":"mgmtRequest in BaseServiceImpl "}"""   ) )

 }

def serviceRequest() = ServiceCall {  request =>

   var requestDirective = Json.toJson(Json.parse(request.toString())).\("requestDirective").as[JsValue]
   var requestBody = Json.toJson(Json.parse(request.toString())).\("requestBody").as[JsValue]
   var responseDirective = Json.toJson(Json.parse(request.toString())).\("responseDirective").as[JsValue]

   if (!simulateService)
     TraceableActorRef(serviceTopActor) ! ServiceRequest(requestDirective,requestBody,responseDirective)(ActorTraceData())
   else
     callServiceSimulator (ServiceRequest(requestDirective,requestBody,responseDirective)(ActorTraceData()))

   Future.successful( Json.parse("""{"request":"serviceRequest in BaseServiceImpl"}"""  ))

 }
 def callServiceSimulator(serviceRequest: ServiceRequest): Unit ={

 }
}
