package com.marcel.trip.impl.commands

import java.util.UUID

import akka.Done
import com.marcel.trip.api.events.TripEventTypes.TripEventType
import com.marcel.trip.api.models.TripStatuses.TripStatus
import play.api.libs.json.Json

case class UpdateTrip(
  id: UUID,
  status: TripStatus,
  driver: UUID,
) extends TripCommand[Done]

object UpdateTrip {
  implicit val format = Json.format[UpdateTrip]
}