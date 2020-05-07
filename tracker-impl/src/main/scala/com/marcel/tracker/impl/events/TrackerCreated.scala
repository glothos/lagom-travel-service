package com.marcel.tracker.impl.events

import java.util.UUID

import play.api.libs.json.Json

case class TrackerCreated(
  userId: UUID,
  lat: Double,
  lng: Double,
  currentRegion: Long,
) extends TrackerEvent

object TrackerCreated {
  implicit val format = Json.format[TrackerCreated]
}
