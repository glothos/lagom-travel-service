package com.marcel.trip.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.marcel.trip.impl.commands.CreateTrip
import com.marcel.trip.impl.events.TripUpdated
import com.marcel.trip.impl.aggregates.TripAggregate
import com.marcel.trip.impl.commands.{CreateTrip, UpdateTrip}
import com.marcel.trip.impl.events.{TripCreated, TripUpdated}

import scala.collection.immutable

object TripSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: immutable.Seq[JsonSerializer[_]] = List (
    // Aggregate
    JsonSerializer[TripAggregate],
    // Commands
    JsonSerializer[CreateTrip],
    JsonSerializer[UpdateTrip],
    // Events
    JsonSerializer[TripCreated],
    JsonSerializer[TripUpdated]
  )

}

