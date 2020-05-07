package com.marcel.trip.api

import java.util.UUID

import akka.{Done, NotUsed}
import akka.stream.scaladsl.Source
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import com.marcel.trip.api.models.EstimationRequest
import com.marcel.tracker.api.events.TrackerKafkaEvent
import com.marcel.trip.api.events.TripKafkaEvent
import com.marcel.trip.api.models.{TripRequest, TripResponse}

trait TripService extends Service {
  def create: ServiceCall[TripRequest, TripResponse]
  def getSingleTrip(tripId: UUID): ServiceCall[NotUsed, Done]
//  def prices: ServiceCall[EstimationRequest, EstimationResponse]
  // TODO: This is just a representation of what should happen, not the implementation itself
//  def statusUpdated(tripId: UUID): ServiceCall[NotUsed, Source[Trip, NotUsed]]

  def tripEvents(): Topic[TripKafkaEvent]

  override def descriptor: Descriptor = {
    import Service._
    named("trip").withCalls(
      restCall(Method.POST, "/trips", create _),
      restCall(Method.GET, "/trips/:tripId", getSingleTrip _)
//      restCall(Method.POST, "/trips/prices", prices _),
      // TODO: pathCall("trips/:tripId", statusUpdated _)f
    ).withTopics(
      topic("TripEvents", tripEvents)
        .addProperty(
          KafkaProperties.partitionKeyStrategy,
          PartitionKeyStrategy[TripKafkaEvent](_.id.toString)
        )
    ).withAutoAcl(true)
  }
}
