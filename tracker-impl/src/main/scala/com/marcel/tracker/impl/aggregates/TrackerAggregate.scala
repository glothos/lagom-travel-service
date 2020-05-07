package com.marcel.tracker.impl.aggregates

import java.util.UUID

import play.api.libs.json.Json

case class TrackerAggregate(
  userId: UUID,
  lat: Double,
  lng: Double,
  h37: Option[Long],
  kRing7: Option[List[Long]],
  timestamp: Long,
  currentRegion: Long,
)

object TrackerAggregate {
  implicit val format = Json.format[TrackerAggregate]
}
