package com.marcel.trip.impl.events

import java.util.UUID

import com.marcel.trip.api.models.TripStatuses.TripStatus
import play.api.libs.json.Json

case class TripUpdated(
  id: UUID,
  newStatus: TripStatus,
  newDriver: UUID,
) extends TripEvent

object TripUpdated {
  implicit val format = Json.format[TripUpdated]
}