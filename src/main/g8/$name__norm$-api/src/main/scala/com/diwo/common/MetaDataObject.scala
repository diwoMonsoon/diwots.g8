/*
 * Module : servicesMonitor-api
 * File   : MetaDataObject.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common

import scala.collection.mutable

class MetaDataObject  extends KnowledgeObject{
  var mkObjects: mutable.HashMap[String,Any] = mutable.HashMap.empty[String,Any]

  def getKO(key:String ):Any={
    return(Some(mkObjects(key)))
  }
  def addKO(key:String,obj:Any): Unit ={
    ko_id=key
    mkObjects(key) = obj
  }

}
