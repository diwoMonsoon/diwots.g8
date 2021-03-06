/*
 * Module : servicesDiscoverer-impl
 * File   : servicesDiscovererActor.scala
 * Last Modified : 6/18/19 9:13 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package $package$

import java.io.{BufferedWriter, File, FileWriter}
import java.sql.DriverManager

import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import com.diwo.common.{ServiceRequest, ServiceTopActor}
import com.typesafe.config.ConfigFactory
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import java.sql.ResultSet

import brave.play.actor.ActorTraceSupport.TraceableActor
import com.diwo.common.api.DeployMetaData

import scala.util.Random
//import com.github.levkhomich.akka.tracing.thrift.Span
import com.lightbend.lagom.scaladsl.api.transport.{RequestHeader, ResponseHeader}
//import com.github.levkhomich.akka.tracing.{ActorTracing, TracingActorLogging, TracingSupport}

import brave.play.actor.ActorTraceSupport._
import brave.play.{ ZipkinTraceServiceLike}

import java.util.concurrent.TimeUnit

import akka.actor._
import akka.util.Timeout
import brave.play.ZipkinTraceServiceLike
import brave.play.actor.ActorTraceSupport._

class $name;format="Camel"$ServiceTopActor( override implicit val tracer: ZipkinTraceServiceLike) extends ServiceTopActor with DeployMetaData with TraceableActor {

  implicit val timeout = Timeout(10 seconds)

  val config = ConfigFactory.load()

  var serviceReference :mutable.HashMap[String,String ] = mutable.HashMap.empty[String,String]
  var serviceState :mutable.HashMap[String,String ] = mutable.HashMap.empty[String,String]

  override def preStart(): Unit = {
    super.preStart() //initializeMetaDataConfig()
    println(self.path.name)
  }

  override def preMetaDataLoad(configName:String) ={
      println("preMetaDataLoad")

  }
  override def reloadMetaData(configName:String)={
    println("reloadMetaData")
  }
  override def postMetaDataLoad(configName:String)={
    println("postMetaDataLoad")
  }


  override def normalBehavior = {
    case s:ServiceRequest =>
      var requestBody = s.requestBody
      import scala.concurrent.ExecutionContext.Implicits.global
      var cmdName =   requestBody.\("commandName").as[String]
      cmdName match {
        case "deployMetaData" =>
          super.metaDataDeployBehavior(s)
        //  extend the result from deploymentment in the implementation actor
        var result :JsValue= Json.toJson("{}")
          sender() ! result
        

      }
  }

 // override def metaDataDeployBehavior: Receive = super.metaDataDeployBehavior
  //override def receive = metaDataDeployBehavior orElse normalBehavior 
  //handling of the metadata deployment is part of normal behaviour as it is part of generic ServiceRequest instead of 
  // management request
  
  override def receive =  normalBehavior 
  extendedRunningState = receive
}

