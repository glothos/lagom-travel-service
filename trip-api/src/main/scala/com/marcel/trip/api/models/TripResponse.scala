package com.marcel.trip.api.models

import java.util.UUID

import com.marcel.trip.api.events.TripEventTypes.TripEventType
import Categories.Category
import com.marcel.trip.api.events.TripEventTypes
import play.api.libs.json._

sealed trait TripResponse {
  def id: UUID
  def origin: List[Double]
  def destination: List[Double]
  def event: TripEventTypes.TripEventType
  def category: Category
}

case class TripNoDriver(
  id: UUID,
  origin: List[Double],
  destination: List[Double],
  event: TripEventTypes.TripEventType,
  category: Category
) extends TripResponse

case class TripWithDriver(
  id: UUID,
  origin: List[Double],
  destination: List[Double],
  event: TripEventTypes.TripEventType,
  driver: UUID,
  category: Category
) extends TripResponse

case object TripNoDriver {
  implicit val tripNoDriverFormat = Json.format[TripNoDriver]
}

case object TripWithDriver {
  implicit val tripWithDriverFormat = Json.format[TripWithDriver]
}
object TripResponse {
  implicit val reads: Reads[TripResponse] = {
    (__ \ "event").read[TripEventType].flatMap {
      case TripEventTypes.REQUESTED   => implicitly[Reads[TripNoDriver]].map(identity)
      case TripEventTypes.DRIVER_ACCEPTED => implicitly[Reads[TripWithDriver]].map(identity)
      case other           => Reads(_ => JsError(s"Unknown event type $other"))
    }
  }
  implicit val writes: Writes[TripResponse] = Writes { event =>
    val (jsValue, eventType) = event match {
      case m: TripNoDriver   => (Json.toJson(m)(TripNoDriver.tripNoDriverFormat), TripEventTypes.MATCHING)
      case m: TripWithDriver => (Json.toJson(m)(TripWithDriver.tripWithDriverFormat), TripEventTypes.DRIVER_ACCEPTED)
    }
    jsValue.transform(__.json.update((__ \ 'event).json.put(JsString(eventType.toString)))).get
  }
}

