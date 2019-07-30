/*
 * Module : servicesMonitor-api
 * File   : BaseServiceActor.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common

import akka.MessageTracingActor
import akka.actor.Stash

//abstract class BaseServiceActor(implicit tracer: brave.play.ZipkinTraceServiceLike) extends MessageTracingActor with Stash {
abstract class BaseServiceActor(implicit tracer: brave.play.ZipkinTraceServiceLike) extends MessageTracingActor with Stash {


}
