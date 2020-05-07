package com.marcel.trip.impl.events

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag, AggregateEventTagger}

trait TripEvent extends AggregateEvent[TripEvent] {
  override def aggregateTag: AggregateEventTagger[TripEvent] = TripEvent.Tag
}

object TripEvent {
  val NumShards = 3
  val Tag: AggregateEventShards[TripEvent] = AggregateEventTag.sharded[TripEvent](NumShards)
}