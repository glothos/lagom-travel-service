package com.marcel.tracker.impl

import com.lightbend.lagom.scaladsl.api.ServiceLocator
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.pubsub.PubSubComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext, LagomApplicationLoader, LagomServer}
import com.marcel.tracker.api.TrackerService
import com.softwaremill.macwire.wire
import play.api.libs.ws.ahc.AhcWSComponents

class TrackerApplicationLoader extends LagomApplicationLoader {
  override def load(context: LagomApplicationContext): LagomApplication = {
    new TrackerApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }
  }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication = {
    new TrackerApplication(context) with LagomDevModeComponents
  }
}

abstract class TrackerApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
  with PubSubComponents
  with CassandraPersistenceComponents
  with AhcWSComponents
  with LagomKafkaComponents{
  override lazy val lagomServer: LagomServer = serverFor[TrackerService](wire[TrackerImpl])
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = TrackerSerializerRegistry
  persistentEntityRegistry.register(wire[TrackerEntity])
  readSide.register(wire[TrackerReadSideProcessor])
  lazy val readSideConnector: TrackerReadSideConnector = wire[TrackerReadSideConnector]
}