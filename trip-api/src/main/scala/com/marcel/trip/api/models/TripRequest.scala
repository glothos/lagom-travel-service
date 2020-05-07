package com.marcel.trip.api.models

import java.util.UUID

import play.api.libs.json.Json

case class TripRequest(
  origin: List[Double],
  destination: List[Double],
  paymentMethod: String,
  estimatedPrice: Double,
  passengerId: UUID,
  category: Categories.Category,
)

object TripRequest {
  implicit val format = Json.format[TripRequest]
}
