package com.marcel.trip.api.models

import com.marcel.jsonformats.JsonFormats.enumFormat
import play.api.libs.json.Format

object TripStatuses extends Enumeration {
  type TripStatus = Value
  val
    MATCHING,
    DRIVER_EN_ROUTE,
    EN_ROUTE_TO_DESTINATION,
    COMPLETED,
    IN_PROGRESS = Value
  implicit val format: Format[TripStatus] = enumFormat(TripStatuses)
}
