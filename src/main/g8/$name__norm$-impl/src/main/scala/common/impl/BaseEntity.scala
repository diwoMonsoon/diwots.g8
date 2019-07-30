/*
 * Module : servicesMonitor-impl
 * File   : BaseEntity.scala
 * Last Modified : 7/26/19, 10:57 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */

package com.diwo.common.impl

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq
 trait BaseCommand[R] extends ReplyType[R]

case class Hello(name: String) extends BaseCommand[String]
//
object Hello {
  //
  //  /**
  //    * Format for the hello command.
  //    *
  //    * Persistent entities get sharded across the cluster. This means commands
  //    * may be sent over the network to the node where the entity lives if the
  //    * entity is not on the same node that the command was issued from. To do
  //    * that, a JSON format needs to be declared so the command can be serialized
  //    * and deserialized.
  //    */
  implicit val format: Format[Hello] = Json.format
}
//
///**
//  * Akka serialization, used by both persistence and remoting, needs to have
//  * serializers registered for every type serialized or deserialized. While it's
//  * possible to use any serializer you want for Akka messages, out of the box
//  * Lagom provides support for JSON, via this registry abstraction.
//  *
//  * The serializers are registered here, and then provided to Lagom in the
//  * application loader.
//  */
object BaseSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[Hello]

  )
}
