/*
 * Module : servicesMonitor-api
 * File   : URIGenerator.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common

object URIGenerator {

  def get(key:String,module:String): String ={
    val str_meta = key +"_" + module
    str_meta
  }

  //TODO  return list object
  def getList(key:String,module:String,urischeme:String): List[String] ={
    val str_meta = key +"_" + urischeme + module
   List(str_meta)
  }
}

