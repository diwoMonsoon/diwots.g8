/*
 * Module : servicesMonitor-api
 * File   : BaseService.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */
package com.diwo.common.api

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.JsValue

object BaseService  {

  var serviceName = ConfigFactory.load().getString("ServiceName")
  val TOPIC_NAME = "" + serviceName
  // "ServicesBase"
}

/**
  * The ServicesBase service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the ServicesBaseService.
  */
trait BaseService extends Service  with LazyLogging {
  def hello(id: String): ServiceCall[NotUsed, String]

  def serviceRequest(): ServiceCall[JsValue, JsValue]

  def mgmtRequest(): ServiceCall[JsValue, JsValue]

  override final def descriptor: Descriptor = {
    import Service._
    // @formatter:off
    named(BaseService.serviceName)
      .withCalls(
        pathCall("/api/hello/:id", hello _),
        restCall(Method.POST, "/api/serviceRequest/", serviceRequest _),
        restCall(Method.POST, "/api/mgmtRequest/", mgmtRequest _)
      )
      .withAutoAcl(true)
    // @formatter:on
  }
}
