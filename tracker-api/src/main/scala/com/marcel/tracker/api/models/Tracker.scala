package com.marcel.tracker.api.models

import java.util.UUID

import TrackerStatuses.TrackerStatus
import play.api.libs.json.Json

case class Tracker(
  userId: UUID,
  lat: Double,
  lng: Double,
  timestamp: Long,
  currentRegion: Option[Long],
  status: TrackerStatus
)

object Tracker {
  implicit val format = Json.format[Tracker]
}
