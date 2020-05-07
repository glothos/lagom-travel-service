package com.marcel.tracker.impl.events

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag, AggregateEventTagger}

trait TrackerEvent extends AggregateEvent[TrackerEvent] {
  override def aggregateTag: AggregateEventTagger[TrackerEvent] = TrackerEvent.Tag
}

object TrackerEvent {
  val NumShards = 10
  val Tag: AggregateEventShards[TrackerEvent] = AggregateEventTag.sharded[TrackerEvent](NumShards)
}
