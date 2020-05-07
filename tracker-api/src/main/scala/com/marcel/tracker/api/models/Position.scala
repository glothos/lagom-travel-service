package com.marcel.tracker.api.models

import play.api.libs.json.Json

case class Position(
  lat: Double,
  lng: Double,
  timestamp: Long
)

object Position {
  implicit val format = Json.format[Position]
}