package com.marcel.trip.api.events

import play.api.libs.json._
import com.marcel.jsonformats.JsonFormats._

object TripEventTypes extends Enumeration {
  type TripEventType = Value
  val
    REQUESTED,
    MATCHING,
    DRIVER_ACCEPTED,
    DRIVER_EN_ROUTE,
    DRIVER_ARRIVED,
    DRIVER_CANCELLED,
    PASSENGER_CANCELLED,
    INITIATED,
    EN_ROUTE_TO_DESTINATION,
    ARRIVED_IN_DESTINATION,
    COMPLETED,
    COMPLETED_PREMATURELY,
    WAYPOINT_ADDED,
    WAYPOINT_REMOVED = Value

  implicit val format: Format[TripEventType] = enumFormat(TripEventTypes)
}
