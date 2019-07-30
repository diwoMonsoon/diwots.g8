package $package$.api


/**
  * The $name$ service interface.
  * <p>
  * This describes everything that Lagom needs to know about how to serve and
  * consume the $name;format="Camel"$Service.
  */

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import play.api.libs.json.{Format, Json}
import akka.{Done, NotUsed}
import com.diwo.common.api.BaseService
import com.lightbend.lagom.scaladsl.api.Service.restCall
import com.lightbend.lagom.scaladsl.api.transport.{HeaderFilter, Method}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import com.typesafe.config.ConfigFactory
import play.api.libs.json.JsValue

object $name;format="Camel"$Service  {
  var serviceName = ConfigFactory.load().getString("ServiceName")
  val TOPIC_NAME = "" + serviceName
}
trait $name;format="Camel"$Service extends Service {


}

