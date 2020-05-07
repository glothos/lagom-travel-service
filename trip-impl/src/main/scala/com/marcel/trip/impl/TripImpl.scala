package com.marcel.trip.impl

import java.util.UUID

import akka.{Done, NotUsed}
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.pubsub.PubSubRegistry
import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import com.marcel.trip.api.events.TripEventTypes
import com.marcel.trip.api.models.TripResponse
import com.marcel.trip.impl.commands.CreateTrip
import com.marcel.trip.impl.events.TripFound
import com.marcel.tracker.api.TrackerService
import com.marcel.trip.api.{TripService, events}
import com.marcel.trip.api.events.{TripEventTypes, TripKafkaEvent}
import com.marcel.trip.api.models.{TripRequest, TripResponse}
import com.marcel.trip.impl.commands.{CreateTrip, GetTrip}
import com.marcel.trip.impl.events.{TripCreated, TripEvent, TripFound, TripUpdated}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

class TripImpl(
  registry: PersistentEntityRegistry,
  pubSubRegistry: PubSubRegistry,
)(implicit ec: ExecutionContext, mat: Materializer) extends TripService {
  private def entityRef(id: UUID) =
    registry.refFor[TripEntity](id.toString)

//  private def getTokenFromHeader[Request, Response](serviceCall: Long => ServerServiceCall[Request, Response]) =
//    ServerServiceCall.composeAsync { requestHeader =>
//      val phoneLookup = requestHeader.getHeader("Authorization")
//        .map(token =>
//          readSideConnector
//            .getPhoneNumberFromToken(UUID.fromString(token))
//        ).getOrElse(Future.successful(None))
//      phoneLookup.map {
//        case Some(number) => serviceCall(number)
//        case None => throw Forbidden("No token present or token is invalid")
//      }
//    }

  override def tripEvents(): Topic[TripKafkaEvent] =
    TopicProducer.taggedStreamWithOffset(TripEvent.Tag.allTags.to[immutable.Seq]) {(tag, offset) =>
      registry.eventStream(tag, offset)
        .map {e =>
          e.event match {
            case TripFound(_, o, d, Some(dId), ev, cat) =>
              (events.TripKafkaEvent(
                ev,
                Some(UUID.fromString(e.entityId)),
                Map(
                  "origin" -> o.mkString("[", ", ", "]"),
                  "destination" -> d.mkString("[", ", ", "]"),
                  "driver" -> dId.toString,
                  "category" -> cat.toString
                )
              ), e.offset)
            case TripCreated(_, _, origin, destination, _, category, _ ) =>
              (TripKafkaEvent(
                TripEventTypes.MATCHING,
                Some(UUID.fromString(e.entityId)),
                Map(
                  "origin" -> origin.mkString("[", ", ", "]"),
                  "destination" -> destination.mkString("[", ", ", "]"),
                  "category" -> category.toString
                )
              ), e.offset)
            case TripUpdated(_, newStatus, newDriver) =>
              (TripKafkaEvent(
                TripEventTypes.DRIVER_ACCEPTED,
                Some(UUID.fromString(e.entityId)),
                Map(
                  "status" -> newStatus.toString,
                  "driver" -> newDriver.toString,
                )
              ), e.offset)
          }
        }
    }
  private def getTokenFromHeader[Request, Response](serviceCall: UUID => ServerServiceCall[Request, Response]) =
    ServerServiceCall.composeAsync { requestHeader =>
      val tokenLookup = requestHeader.getHeader("Authorization").get
      Future.successful(serviceCall(UUID.fromString(tokenLookup)))
    }
  // TODO: Different type of users. Hotel should have a list of trips
  override def create: ServiceCall[TripRequest, TripResponse] = getTokenFromHeader { token =>
    ServerServiceCall { req =>
      val newTripId = UUID.randomUUID()
      entityRef(newTripId).ask(
        CreateTrip(
          newTripId,
          req.passengerId,
          req.origin,
          req.destination,
          req.estimatedPrice,
          req.category,
          req.paymentMethod,
        )
      ).map( t => t)
    }
  }

  override def getSingleTrip(tripId: UUID): ServiceCall[NotUsed, Done] =
    ServiceCall { _ =>
      entityRef(tripId).ask(GetTrip(Some(tripId))).map(_ => Done)
    }
}
