/*
 * Module : servicesMonitor-api
 * File   : BaseActor.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common

import akka.actor.{Actor, Stash}
import brave.play.ZipkinTraceServiceLike
import brave.play.actor.ActorTraceSupport.TraceableActor
import com.typesafe.scalalogging.LazyLogging

abstract class BaseActor (implicit val tracer: ZipkinTraceServiceLike)extends Actor with LazyLogging with Stash with  TraceableActor {

}
