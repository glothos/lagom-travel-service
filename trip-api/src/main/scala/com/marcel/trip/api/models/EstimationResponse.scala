package com.marcel.trip.api.models

import Categories.Category
import play.api.libs.json.Json

case class EstimationResponse(
  estimatedPrice: Double,
  duration: Long,
  categories: List[Category]
)

object EstimationResponse {
  implicit val format = Json.format[EstimationResponse]
}