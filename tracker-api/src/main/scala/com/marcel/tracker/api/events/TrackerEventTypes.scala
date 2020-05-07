package com.marcel.tracker.api.events

import play.api.libs.json._
import com.marcel.jsonformats.JsonFormats._

object TrackerEventTypes extends Enumeration {
  type TrackerEventType = Value
  val POS_RECEIVED, POS_UPDATED = Value

  implicit val format: Format[TrackerEventType] = enumFormat(TrackerEventTypes)
}
