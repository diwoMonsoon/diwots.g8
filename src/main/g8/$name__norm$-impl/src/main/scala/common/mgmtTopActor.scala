/*
 * Module : servicesMonitor-impl
 * File   : mgmtTopActor.scala
 * Last Modified : 7/26/19, 10:57 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common

import com.diwo.common.ServiceRequest
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.collection.mutable
//import com.diwo.ServicesBase.registry.{addServiceReference, getServiceReference}
import com.typesafe.scalalogging.LazyLogging


class mgmtTopActor extends BaseMgmtActor with LazyLogging {


  logger.info(getClass().getClassLoader().getResource("metadata.json").getPath.toString)


  //val bufferedSource = scala.io.Source.fromResource("metadata.json")
  val bufferedSource = scala.io.Source.fromInputStream(getClass().getClassLoader().getResourceAsStream("metadata.json"))


  val metadataConfig = bufferedSource.mkString
  var MetaActor : mutable.HashMap[String,String] = mutable.HashMap.empty[String,String]
  def getNewRequestId()={
    java.util.UUID.randomUUID.toString
  }

  import java.time.Instant
  def getTimeStamp () {
    Instant.now.getEpochSecond
  }

  def getNewRequestDirective(): JsValue= {
    var requestDirective: JsValue = Json.toJson("{}")
    var requestId = getNewRequestId()
    var requestTS = getTimeStamp
    var t: JsObject = requestDirective.as[JsObject] ++ Json.obj("requestId" -> s"$requestId") ++ Json.obj("requestTS" -> s"$requestTS")
    t.as[JsValue]
  }

  def initialize(): Unit = {
//    logger.info("mgmtTopActor Initialized")
//    implicit val metadataserializer  = Json.format[MetaDataActorReference]
//    val metadataActorRef = Json.parse(metadataConfig).as[Seq[MetaDataActorReference]]
//    for ( m <- metadataActorRef){
//      MetaActor(m.configName) = m.serviceName
//
//      var requestBody :JsValue = Json.toJson("{}")
//
//      var newRequestBody: JsObject = requestBody.as[JsObject] ++ Json.obj("serviceName" -> s"${BaseService.serviceName}")       ++ Json.obj("configName" ->m.configName)         ++ Json.obj("configName" ->m.instanceId)
//
//      var request = new ServiceRequest = new ServiceRequest(getNewRequestDirective,newRequestBody, Json.toJson("{}"))
//
//      //requestMetaDataService(request)
//
//    }
//    logger.info(MetaActor)

  }

  override def receive: Receive={
    case "initialize" => initialize()
    case s:ServiceRequest =>
      var apiName =   s.requestBody.\("commandName").as[String]
        apiName match {
          case "enableSimulation" =>
            logger.info("Simulating request " + s.requestBody.\("serviceName").as[String] )
          case "disableSimulation" =>
            //var svcRequest = getServiceReference(s.requestDirective.\("serviceName").as[String])
            logger.info("Simulating request " + s.requestBody.\("serviceName").as[String])
          case _ =>
             logger.info("Unknown option " + apiName + " in "+self.path.name + " from " +sender().path.name)

        }

  }
}

