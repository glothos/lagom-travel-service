package com.marcel.tracker.impl.events

import java.util.UUID

import play.api.libs.json.Json

case class PositionSynced(
  userId: UUID,
  lat: Double,
  lng: Double,
  timestamp: Long,
  currentRegion: Long,
) extends TrackerEvent

object PositionSynced {
  implicit val format = Json.format[PositionSynced]
}
