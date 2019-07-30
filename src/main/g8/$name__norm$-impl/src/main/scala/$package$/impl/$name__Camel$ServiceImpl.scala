/*
 * Module : servicesDiscoverer-impl
 * File   : servicesDiscovererServiceImpl.scala
 * Last Modified : 6/17/19 11:50 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */
package $package$.impl

import com.diwo.common.ServiceRequest
import akka.actor.{ActorSystem, Props}
import $package$.api
import $package$.api.$name;format="Camel"$Service
import com.diwo.common.api.BaseService
import com.diwo.common.impl.BaseServiceImpl
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import com.lightbend.lagom.scaladsl.api.transport.{RequestHeader, ResponseHeader}
import com.typesafe.config.{Config, ConfigFactory}
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import akka.pattern.ask
import akka.util.Timeout
import brave.play.actor.ZipkinTraceService
import java.util.concurrent.TimeUnit

import akka.util.Timeout
import brave.play.actor.ActorTraceSupport.{ActorTraceData, TraceableActorRef}




class $name;format="Camel"$ServiceImpl extends BaseServiceImpl with  $name;format="Camel"$Service  {

  println("In $name;format="Camel"$ServiceImpl")

  var baseUrl =  ConfigFactory.load().getString("baseurl")


  object serviceState1 extends Enumeration {
    type serviceState = Value
    val STARTED, INITIALIZED, READY, IN_AGGREGATION, STOPPED = Value
  }


  var state = serviceState1.STARTED

 serviceTopActorName  = ConfigFactory.load().getString("ServiceTopActor")
 simulatorActorName  = ConfigFactory.load().getString("SimulatorActorName")

  override implicit val tracer = new ZipkinTraceService(system, BaseService.serviceName,baseUrl)

  implicit val timeout = Timeout(5000, TimeUnit.MILLISECONDS)


  serviceTopActor = system.actorOf(Props(Class.forName(serviceTopActorName),tracer),name ="$name;format="Camel"$Service_TopActor")
  TraceableActorRef(serviceTopActor) ! "initialize"

  var simulatorActor =  system.actorOf(Props(Class.forName(simulatorActorName)),name = "$name;format="Camel"$Service_serviceTopActor")



  override def serviceRequest() = ServiceCall { request =>
    implicit val timeout = Timeout(15 seconds)
  //  println("In servicesDiscovererServiceImpl-serviceRequest")
    var requestDirective = Json.toJson(Json.parse(request.toString())).\("requestDirective").as[JsValue]
    var requestBody = Json.toJson(Json.parse(request.toString())).\("requestBody").as[JsValue]
    var responseDirective = Json.toJson(Json.parse(request.toString())).\("responseDirective").as[JsValue]
  var result :JsValue= Json.toJson("{}")
    if (!simulateService){
      println("normalmessage")
        val future = TraceableActorRef(serviceTopActor) ? new ServiceRequest(requestDirective, requestBody, responseDirective) (ActorTraceData())
        result = Await.result(future, timeout.duration).asInstanceOf[JsValue]
        Future.successful(result)
    }
    else
    {
      //callServiceSimulator(ServiceRequest(requestDirective, requestBody, responseDirective))
      println("simulate message")
      val future =TraceableActorRef( simulatorActor )? new ServiceRequest(requestDirective, requestBody, responseDirective)(ActorTraceData())
      result = Await.result(future, timeout.duration).asInstanceOf[JsValue]
      println(result)
      Future.successful(result)
    }
  }
}
