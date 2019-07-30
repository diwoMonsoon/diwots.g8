/*
 * Module : servicesMonitor-api
 * File   : UIDGenerator.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common

object UIDGenerator {


  def get(key:String): String ={
    val str_meta = "KO" + key +"_"
    str_meta
  }
  def getNameFromUID(workerUID:String)={
    workerUID.substring("worker_".length)
  }
}

