/*
 * Module : servicesMonitor-api
 * File   : KnowledgeObjectState.scala
 * Last Modified : 7/29/19, 11:32 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common

class KnowledgeObjectState(state:String) {

 private var koState:String = state


 def getState (): String ={
  koState
 }
}
