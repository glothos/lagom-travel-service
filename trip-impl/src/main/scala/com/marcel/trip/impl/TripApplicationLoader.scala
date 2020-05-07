package com.marcel.trip.impl

import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceLocator}
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.pubsub.PubSubComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.marcel.tracker.api.TrackerService
import com.marcel.trip.api.TripService
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents

class TripApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication = {
    new TripApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    new TripApplication(context) with LagomDevModeComponents
  }

  override def describeService: Option[Descriptor] = Some(readDescriptor[TripService])
}

abstract class TripApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with PubSubComponents
    with CassandraPersistenceComponents
    with AhcWSComponents
    with LagomKafkaComponents{
  override lazy val lagomServer: LagomServer = serverFor[TripService](wire[TripImpl])
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = TripSerializerRegistry
  persistentEntityRegistry.register(wire[TripEntity])
}
