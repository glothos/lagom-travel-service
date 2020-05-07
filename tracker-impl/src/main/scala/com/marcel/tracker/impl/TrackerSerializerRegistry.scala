package com.marcel.tracker.impl

import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.marcel.tracker.impl.commands.GetTracker
import com.marcel.tracker.impl.events.PositionSynced
import com.marcel.tracker.impl.aggregates.TrackerAggregate
import com.marcel.tracker.impl.commands.{GetTracker, SyncTracker}
import com.marcel.tracker.impl.events.{PositionSynced, TrackerCreated}

import scala.collection.immutable

object TrackerSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: immutable.Seq[JsonSerializer[_]] = List (
    // Aggregate
    JsonSerializer[TrackerAggregate],
    // Commands
    JsonSerializer[SyncTracker],
    JsonSerializer[GetTracker],
    // Events
    JsonSerializer[PositionSynced],
    JsonSerializer[TrackerCreated]
  )

}
