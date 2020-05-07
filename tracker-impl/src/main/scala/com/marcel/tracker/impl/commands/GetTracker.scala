package com.marcel.tracker.impl.commands

import java.util.UUID

import com.marcel.tracker.api.models.Tracker
import play.api.libs.json.Json

case class GetTracker(userId: UUID) extends TrackerCommand[Tracker]

object GetTracker {
  implicit val format = Json.format[GetTracker]
}
