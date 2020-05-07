package com.marcel.tracker.api.models

import play.api.libs.json.Format
import com.marcel.jsonformats.JsonFormats._

object TrackerStatuses extends Enumeration {
  type TrackerStatus = Value
  val AVAILABLE,
  UNAVAILABLE,
  OFFLINE = Value
  implicit val format: Format[TrackerStatus] = enumFormat(TrackerStatuses)
}
