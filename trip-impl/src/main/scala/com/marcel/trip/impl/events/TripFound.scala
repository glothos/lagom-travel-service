package com.marcel.trip.impl.events

import java.util.UUID

import com.marcel.trip.api.events.TripEventTypes.TripEventType
import com.marcel.trip.api.models.Categories.Category
import play.api.libs.json.Json

case class TripFound(
  id: UUID,
  origin: List[Double],
  destination: List[Double],
  driver: Option[UUID],
  event: TripEventType,
  category: Category
) extends TripEvent

object TripFound {
  implicit val format = Json.format[TripFound]
}
