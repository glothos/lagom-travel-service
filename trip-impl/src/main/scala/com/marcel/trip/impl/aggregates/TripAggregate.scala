package com.marcel.trip.impl.aggregates

import java.util.UUID

import play.api.libs.json.Json
import com.marcel.trip.api.models.Categories.Category
import com.marcel.trip.api.models.TripStatuses.TripStatus
import com.marcel.tracker.api.models.Tracker

case class TripAggregate(
  id: UUID,
  origin: List[Double],
  destination: List[Double],
  waypoints: Option[List[List[Double]]],
  status: TripStatus,
  category: Category,
  driver: Option[UUID],
  passenger: UUID,
  paymentMethod: String,
  estimatedPrice: Double,
  finalPrice: Option[Double],
  startedAt: Option[Long],
  completedAt: Option[Long],
  createdAt: Long,
  updatedAt: Option[Long],
)

object TripAggregate {
  implicit val format = Json.format[TripAggregate]
}
