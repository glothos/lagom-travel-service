package com.marcel.trip.api.models

import play.api.libs.json.Json

case class EstimationRequest(
  origin: Double,
  destination: Double,
  paymentMethod: Double,
)

object EstimationRequest {
  implicit val format = Json.format[EstimationRequest]
}
