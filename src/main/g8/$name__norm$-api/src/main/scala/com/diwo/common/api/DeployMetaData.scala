/*
 * Module : servicesMonitor-api
 * File   : DeployMetaData.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common.api

import akka.actor.Actor
import com.diwo.common.ServiceRequest
import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.JsValue


trait DeployMetaData extends Actor with LazyLogging{
  var metadataRequired: List[String] = List("")
//  var configName:String =""

  def preMetaDataLoad(configName:String) = {
    logger.info( self.path.name + " Pre loading Metadata "+ configName +"received ")

  }

  def reloadMetaData(configName:String) = {
    //automatic reloading

  }

  def postMetaDataLoad(configName:String) = {

  }
  def metaDataDeployBehavior: Receive = {


    case s:ServiceRequest =>

      logger.error("in receive" )
      var requestBody = s.requestBody
      var cmdName = requestBody.\("commandName").as[String]
      cmdName match {
        case "deployMetaData" =>
          var configName =   requestBody.\("configName").as[String]
          var metaData =   requestBody.\("metaData").as[JsValue]

          //        if ( sender() != self ){
          if (metadataRequired.contains(configName)) {
            preMetaDataLoad(configName)
            reloadMetaData(configName)
            postMetaDataLoad(configName)
          }

        //  logger.info("sending to all childern",self.path.name)
          var alist = context.actorSelection(self.path+"/*")
          //logger.info(alist.pathString)
          alist ! s
       //   logger.info("Received new metadata")
      }
  }
}
