/*
 * Module : servicesDiscoverer-impl
 * File   : servicesDiscovererLoader.scala
 * Last Modified : 6/17/19 11:50 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package $package$.impl


import $package$.api.$name;format="Camel"$Service
import com.diwo.common.api.BaseService
import com.diwo.common.impl.{BaseApplication, BaseLoader, BaseServiceImpl}
import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomServer}
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents


  class  $name;format="Camel"$Loader extends BaseLoader {
    override def load(context: LagomApplicationContext): LagomApplication =
      new BaseApplication(context) {
        override def serviceLocator: ServiceLocator = NoServiceLocator
      }

    override def loadDevMode(context: LagomApplicationContext): LagomApplication =
        new $name;format="Camel"$Application(context) with LagomDevModeComponents


    override def describeService = Some(readDescriptor[servicesDiscovererService])
  }
  abstract class $name;format="Camel"$Application(context: LagomApplicationContext)
    extends BaseApplication(context)
      with AhcWSComponents {

    override lazy val lagomServer: LagomServer = serverFor[$name;format="Camel"$Service](wire[$name;format="Camel"$ServiceImpl])


  }

