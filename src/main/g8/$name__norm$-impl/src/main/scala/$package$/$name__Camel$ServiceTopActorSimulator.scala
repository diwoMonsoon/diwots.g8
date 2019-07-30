/*
 * Module : servicesDiscoverer-impl
 * File   : servicesDiscovererSimulator.scala
 * Last Modified : 6/25/19 1:21 PM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.test

import akka.actor.Actor
import akka.util.Timeout
import com.diwo.common.ServiceRequest
import com.lightbend.lagom.scaladsl.api.transport.ResponseHeader
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.duration._

class $name;format="Camel"$ServiceTopActorSimulator extends Actor  {

  //  private val metadataInitActor = context.actorOf(Props(new MetadataInitActor), "MetadataInitActor")
  //  val MdBaseConnectActor = context.actorOf(Props(new JDBCConnectionActor), "JDBCConnectionActor1")
  implicit val timeout = Timeout(10 seconds)
  override def receive: Receive={
    case s:ServiceRequest =>
      println("SimulatorService Request received ",s)
      var requestBody = s.requestBody
      var cmdName =   requestBody.\("commandName").as[String]
      println(cmdName)
      cmdName match {
     
        case _=>
          Future.successful(Json.parse("""{"request":" simulating unknown request "}""" ))
      }
  }
}

