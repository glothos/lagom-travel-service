package com.marcel.trip.impl.commands

import java.util.UUID

import akka.Done
import com.marcel.trip.api.models.TripResponse
import play.api.libs.json.Json

case class GetTrip(id: Option[UUID]) extends TripCommand[Done]

object GetTrip {
  implicit val format = Json.format[GetTrip]
}