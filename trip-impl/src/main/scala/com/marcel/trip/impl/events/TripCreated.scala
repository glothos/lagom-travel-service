package com.marcel.trip.impl.events

import java.util.UUID

import com.marcel.trip.api.models.Categories
import play.api.libs.json.Json

case class TripCreated(
  id: UUID,
  passenger: UUID,
  origin: List[Double],
  destination: List[Double],
  estimatedPrice: Double,
  category: Categories.Category,
  paymentMethod: String,
) extends TripEvent

object TripCreated {
  implicit val format = Json.format[TripCreated]
}