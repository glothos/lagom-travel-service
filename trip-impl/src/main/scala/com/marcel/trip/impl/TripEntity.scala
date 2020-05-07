package com.marcel.trip.impl

import java.util.UUID

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.marcel.trip.api.models.TripStatuses.TripStatus
import com.marcel.trip.api.models.TripWithDriver
import com.marcel.trip.impl.commands.TripCommand
import com.marcel.trip.impl.events.TripFound
import com.marcel.trip.api.events.TripEventTypes
import com.marcel.trip.api.models.{TripNoDriver, TripResponse, TripStatuses}
import com.marcel.trip.impl.aggregates.TripAggregate
import com.marcel.trip.impl.commands.{CreateTrip, GetTrip, TripCommand, UpdateTrip}
import com.marcel.trip.impl.events.{TripCreated, TripEvent, TripFound, TripUpdated}

class TripEntity extends PersistentEntity {

  override type Command = TripCommand[_]
  override type Event = TripEvent

  override type State = Option[TripAggregate]

  override def initialState: Option[TripAggregate] = None

  override def behavior: Behavior = {
    case None => createTrip
    case Some(TripAggregate(_, _, _, _, _, _, _, _, _, _, _, _, _, _, _)) => updateTrip
  }

  private def getEventByStatus(tripStatus: TripStatus) = tripStatus match {
    case TripStatuses.DRIVER_EN_ROUTE => TripEventTypes.DRIVER_ACCEPTED
    case TripStatuses.MATCHING => TripEventTypes.MATCHING
  }

  private def updateTrip: Actions =
    Actions()
    .onCommand[GetTrip, Done] {
      case (GetTrip(_), ctx, state) =>
        state match {
          case Some(TripAggregate(id, o, d, _, st, cat, Some(dId), _, _, _, _, _, _, _, _)) =>
            ctx.thenPersist(TripFound(id, o, d, Some(dId), getEventByStatus(st), cat))(_ => ctx.reply(Done))
          case Some(TripAggregate(id, o, d, _, st, cat, None, _, _, _, _, _, _, _, _)) =>
            ctx.thenPersist(TripFound(id, o, d, None, getEventByStatus(st), cat))(_ => ctx.reply(Done))
        }
    }
    .onCommand[UpdateTrip, Done] {
      case (UpdateTrip(id, status, driver), ctx, state @ Some(_)) =>
        state match {
          case Some(TripAggregate(tripId, _, _, _, _, _, driverId, _, _, _, _, _, _, _, _)) if tripId == id & driverId.isEmpty  =>
            ctx.thenPersist(TripUpdated(tripId, status, driver))(_ => ctx.reply(Done))
        }
      case (_, ctx, _) => ctx.invalidCommand("Invalid command for update"); ctx.done;
    }.onEvent{
      case (TripUpdated(_, s, d), state @ Some(_)) =>
        state.map{_.copy(status = s, driver = Some(d))}
      case _ => None
    }

  private def createTrip: Actions =
    Actions()
    .onCommand[CreateTrip, TripResponse] {
      case (CreateTrip(newId, p, o, d, eP, c, pM), ctx, _) =>
        ctx.thenPersist(TripCreated(newId, p, o, d, eP, c, pM))(t =>
          ctx.reply(TripNoDriver(
            t.id,
            t.origin,
            t.destination,
            TripEventTypes.MATCHING,
            t.category
          ))
        )
      case (_, ctx, _) => ctx.invalidCommand("Command not supported"); ctx.done
  }.onEvent {
      case (TripCreated(id, p, o, d, eP, c, pM), _) =>
        Some(
          TripAggregate(
            id,
            o,
            d,
            None,
            TripStatuses.MATCHING,
            c,
            None,
            p,
            pM,
            eP,
            Some(eP),
            None,
            None,
            System.currentTimeMillis(),
            None,
          )
        )
    }
}
