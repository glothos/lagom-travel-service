package com.marcel.trip.impl.commands

import akka.Done
import com.marcel.trip.api.models.Categories.Category
import play.api.libs.json.Json

case class MatchTrip(
  origin: List[Double],
  category: Category
) extends TripCommand[Done]

object MatchTrip {
  implicit val format = Json.format[MatchTrip]
}