/*
 * Module : servicesMonitor-impl
 * File   : ServiceTopActor.scala
 * Last Modified : 7/29/19, 10:20 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common

import brave.play.ZipkinTraceServiceLike
import brave.play.actor.ActorTraceSupport.TraceableActor
import com.diwo.common.addServiceReference
import com.diwo.common.api.DeployMetaData

import scala.collection.mutable
//import com.diwo.common.{BaseServiceActor, ServiceRequest}
import com.typesafe.config.ConfigFactory
import play.api.libs.json.{Format, Json}

class ServiceTopActor( override implicit val tracer: ZipkinTraceServiceLike) extends KnowledgeAgent with DeployMetaData with TraceableActor  {

  var brokers: String = ""
  var groupId: String=""
  var topic: String =""
  var simulateService = ConfigFactory.load().getBoolean("SimulateService")
  val bufferedSource = scala.io.Source.fromInputStream(getClass().getClassLoader().getResourceAsStream("metadata.json"))

  val metadataConfig = bufferedSource.mkString
  var MetaActor : mutable.HashMap[String,String] = mutable.HashMap.empty[String,String]

  implicit val format: Format[addServiceReference] = Json.format
println("ServiceTopActor")
  override def preStart(): Unit = {
    logger.info("Calling initializeMetaDataConfig")
    //initializeMetaDataConfig()
  }
//  def initializeMetaDataConfig() {
//
//        implicit val metadataserializer  = Json.format[MetaDataConfig]
//        val metadataActorRef = Json.parse(metadataConfig).as[Seq[MetaDataConfig]]
//    logger.info(metadataActorRef)
//        for ( m <- metadataActorRef){
//          MetaActor(m.configName) = m.actorName
//          var requestBody :JsValue = Json.toJson("{}")
//        }
//        logger.info(MetaActor)
//  }

//  def deployMetaData (s:ServiceRequest)={
//      logger.info("Base Service Request received ", s)
//      var requestBody = s.requestBody
//      var cmdName = requestBody.\("commandName").as[String]
//      cmdName match {
//        case "deployMetaData" =>
//
//          //var serviceName =   requestBody.\("serviceName").as[String]
//          //var metaDataName =   requestBody.\("metaDataName").as[String]
//
//          //logger.info("Received new metadata")
//
//      }
//  }



   def normalBehavior :Receive={
     case _ =>
   }
//
 override def receive = normalBehavior orElse metaDataDeployBehavior
}


