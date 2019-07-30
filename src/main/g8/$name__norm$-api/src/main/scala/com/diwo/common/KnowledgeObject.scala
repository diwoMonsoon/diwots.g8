
/*
 * Module : servicesMonitor-api
 * File   : KnowledgeObject.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common

class KnowledgeObjectClass()
case class OntologyURI()

abstract class KnowledgeObject {
   var ko_id:String= ""
//   var  ko_class: KnowledgeObjectClass
 //  val ont_uri: OntologyURI
//   val isTransient:Boolean
//   val  dateCreated:Any
/*  def lastUpdated:Any
  def hasRelationships:Boolean
  def hasConstraints:Boolean
  def isSerializable:Boolean
  def isDeSerializable:Boolean
  def isSelfDescribing:Boolean
*/
//  var loaderAgent:ActorRef=null
//  var loaderType:String=""
//  var loaderClass:KnowledgeObjectLoader=null
//  var updaterAgent:ActorRef=null
//  var updaterType:String=""
//  var updaterClass:KnowledgeObjectUpdater=null
//
//  def getLoaderAgent:ActorRef={loaderAgent}
//  def getUpdaterAgent:ActorRef={ updaterAgent}
//  def getLoaderType:String={loaderType}
//  def getUpdaterType:String={updaterType}
//  def getLoaderClass:KnowledgeObjectLoader={loaderClass}
//  def getUpdaterClass:KnowledgeObjectUpdater={updaterClass}
//
//
//  def setLoaderAgent(actor:ActorRef)={loaderAgent=actor}
//  def setUpdaterAgent(agent:ActorRef)={ updaterAgent=agent}
//  def setLoaderType(ltype:String)={loaderType=ltype}
//  def setUpdaterType(utype:String)={updaterType=utype}
//  def setLoaderClass(lclass:KnowledgeObjectLoader)={loaderClass=lclass}
//  def setUpdaterClass(uclass:KnowledgeObjectUpdater)={updaterClass=uclass}
  var content:Any=null
  def setContent(c:Any): Unit ={
    content =c
  }
//  def copyContent:KnowledgeObjectContent
}

abstract class knowledgeObjectSequence extends KnowledgeObject {


}
abstract class knowledgeObjectHashMap extends KnowledgeObject {


}
