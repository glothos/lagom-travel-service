package com.marcel.tracker.impl.commands

import java.util.UUID

import akka.Done
import play.api.libs.json.Json

case class SyncTracker(
  userId: UUID,
  lat: Double,
  lng: Double,
  timestamp: Long,
) extends TrackerCommand[Done]

object SyncTracker {
  implicit val format = Json.format[SyncTracker]
}
