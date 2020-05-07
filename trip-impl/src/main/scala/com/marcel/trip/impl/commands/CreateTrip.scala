package com.marcel.trip.impl.commands

import java.util.UUID

import akka.Done
import com.marcel.trip.api.models.Categories
import com.marcel.trip.api.models.{Categories, TripResponse}
import play.api.libs.json.Json

case class CreateTrip(
  id: UUID,
  passenger: UUID,
  origin: List[Double],
  destination: List[Double],
  estimatedPrice: Double,
  category: Categories.Category,
  paymentMethod: String,
) extends TripCommand[TripResponse]

object CreateTrip {
  implicit val format = Json.format[CreateTrip]
}
