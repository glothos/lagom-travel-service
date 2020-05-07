package com.marcel.tracker.api

import java.util.UUID

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.deser.MessageSerializer
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import com.marcel.tracker.api.models.Position
import com.marcel.tracker.api.events.TrackerKafkaEvent
import com.marcel.tracker.api.models.{Position, Tracker}

trait TrackerService extends Service {

  def trackPos(userId: UUID): ServiceCall[Source[Position, NotUsed], Source[String, NotUsed]]
  def watchTracker(userId: UUID): ServiceCall[NotUsed, Source[Position, NotUsed]]
  def listTrackers(region: Long): ServiceCall[NotUsed, List[Tracker]]

  def trackerEvents(): Topic[TrackerKafkaEvent]

  override final def descriptor: Descriptor = {
    import Service._
    named("tracker").withCalls(
      pathCall("/trackers/track/:userId?accessToken", trackPos _),
      pathCall("/trackers/watch/:userId", watchTracker _),
      pathCall("/trackers/:region", listTrackers _)
    ).withTopics(
      topic("TrackerEvents", trackerEvents())
        .addProperty(
          KafkaProperties.partitionKeyStrategy,
          PartitionKeyStrategy[TrackerKafkaEvent](_.id.toString)
        )
    ).withAutoAcl(true)
  }

}
