package com.marcel.trip.api.models

import play.api.libs.json.Json
import TripStatuses.TripStatus
import Categories.Category
import com.marcel.tracker.api.models.Tracker
/* TRIP REPRESENTATION */
case class Trip(
  origin: Double,
  waypoints: List[List[Double]],
  destination: Double,
  status: TripStatus,
  category: Category,
  paymentMethod: String,
  estimatedPrice: Double,
  pathTaken: List[Tracker],
  finalPrice: Double,
  startedAt: Long,
  completedAt: Long,
  createdAt: Long,
  updatedAt: Long,
)

object Trip {
  implicit val format = Json.format[Trip]
}