package com.marcel.trip.api.events

import java.util.UUID

import play.api.libs.json.Json

case class TripKafkaEvent(
  event: TripEventTypes.TripEventType,
  id: Option[UUID],
  data: Map[String, String] = Map.empty[String, String]
)

object TripKafkaEvent {
  implicit val format = Json.format[TripKafkaEvent]
}
