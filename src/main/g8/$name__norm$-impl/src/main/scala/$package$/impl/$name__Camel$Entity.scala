

/*
 * Module : servicesDiscoverer-impl
 * File   : servicesDiscovererEntity.scala
 * Last Modified : 6/17/19 10:41 AM
 *
 * Developed by Chandra Keerthy
 *
 * Copyright (c) 2015 - 2019. Loven Systems
 *
 */


package $package$.impl
import java.time.LocalDateTime

import akka.Done
import com.diwo.common.impl.{BaseCommand, Hello}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

sealed trait $name;format="Camel"$Command[R] extends BaseCommand[R]

object $name;format="Camel"$SerializerRegistryextends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[Hello]
  )
}
