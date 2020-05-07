package com.marcel.tracker.api.events

import java.util.UUID

import TrackerEventTypes.TrackerEventType
import play.api.libs.json.Json

case class TrackerKafkaEvent(
  event: TrackerEventType,
  id: UUID,
  data: Map[String, String] = Map.empty[String, String]
)

object TrackerKafkaEvent {
  implicit val format = Json.format[TrackerKafkaEvent]
}
