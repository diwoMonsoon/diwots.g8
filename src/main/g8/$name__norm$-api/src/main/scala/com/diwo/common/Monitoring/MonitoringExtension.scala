/*
 * Module : servicesMonitor-api
 * File   : MonitoringExtension.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common.Monitoring

import akka.actor.ActorSystem

class MonitoringExtension(system:ActorSystem) {

  def start(msgs: MonitoringSupport, service: String) :Unit ={

  }

//  private val holder ={
//    val config = system.settings.config
//    if (config.hasPath(MonitoringService)){
//       system.actorOf((Props ({
//
//       })))
//    }
//  }
}
