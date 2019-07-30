/*
 * Module : servicesMonitor-api
 * File   : MonitoringSupport.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common.Monitoring

trait BaseMonitorSupport extends Any {

  protected def requestId:Long
  protected def spanName:String
  protected def URL:String
  protected def METHOD:String

//  Tags.HTTP_METHOD.set(tracer.activeSpan(), requestHeader.method.name)
//  Tags.HTTP_URL.set(tracer.activeSpan(), requestHeader.uri.toString)

}
trait MonitoringSupport extends  BaseMonitorSupport with Serializable {
  override protected def spanName: String = this.getClass.getSimpleName



}
