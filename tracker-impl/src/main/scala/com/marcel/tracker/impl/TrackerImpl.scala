package com.marcel.tracker.impl

import java.util.UUID

import akka.{Done, NotUsed}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Source}
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.{Message, Topic}
import com.lightbend.lagom.scaladsl.api.transport.NotFound
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import com.marcel.tracker.api.events.TrackerEventTypes
import com.marcel.tracker.api.models.Position
import com.marcel.tracker.impl.commands.GetTracker
import com.marcel.tracker.impl.events.TrackerEvent
import com.marcel.tracker.api.TrackerService
import com.marcel.tracker.api.events.{TrackerEventTypes, TrackerKafkaEvent}
import com.marcel.tracker.api.models.{Position, Tracker}
import com.marcel.tracker.impl.commands.SyncTracker
import com.marcel.tracker.impl.events.{PositionSynced, TrackerCreated, TrackerEvent}
import kafka.utils.immutable
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}

class TrackerImpl(
  registry: PersistentEntityRegistry,
  pubSub: PubSubRegistry,
  readSideConnector: TrackerReadSideConnector,
)(implicit ec: ExecutionContext, mat: Materializer)
  extends TrackerService {
  private val posUpdatedTopic = pubSub.refFor(TopicId[PositionSynced])
  private val log = LoggerFactory.getLogger(classOf[TrackerImpl])

  override def trackerEvents(): Topic[TrackerKafkaEvent] =
    TopicProducer.taggedStreamWithOffset(TrackerEvent.Tag.allTags.to[immutable.Seq]) {(tag, offset) =>
      registry.eventStream(tag, offset)
        .filter { e =>
          e.event match {
            case _: PositionSynced => true
            case _: TrackerCreated => true
            case _ => false
          }
        }.map(ev => ev.event match {
        case TrackerCreated(userId, lat, lng, cr) =>
          log.info(s"Sending tracker created event to Kafka")
          (
            TrackerKafkaEvent(
              TrackerEventTypes.POS_UPDATED,
              userId,
              Map(
                "lat" -> lat.toString,
                "lng" -> lng.toString,
                "timestamp" -> System.currentTimeMillis().toString,
                "currentRegion" -> cr.toString
              )
            ), ev.offset)
          case PositionSynced(userId, lat, lng, timestamp, cr) =>
            (TrackerKafkaEvent(
              TrackerEventTypes.POS_UPDATED,
              userId,
              Map(
                "lat" -> lat.toString,
                "lng" -> lng.toString,
                "timestamp" -> timestamp.toString,
                "currentRegion" -> cr.toString
              )
            ), ev.offset)
      })
    }

  private def entityRef(userId: UUID) =
    registry.refFor[TrackerEntity](userId.toString)

  override def trackPos(userId: UUID): ServiceCall[Source[Position, NotUsed], Source[String, NotUsed]] =
    ServiceCall { req =>
      Future.successful(req.throttle(1, 1.second).mapAsync(8)(tracker =>
        entityRef(userId).ask(SyncTracker(userId, tracker.lat, tracker.lng, tracker.timestamp))
          .map(_ => "Done")
      ))
  }

  override def watchTracker(userId: UUID): ServiceCall[NotUsed, Source[Position, NotUsed]] =
    ServiceCall { _ =>
      Future.successful(posUpdatedTopic.subscriber.map(t => Position(t.lat, t.lng, t.timestamp)))
    }

  override def listTrackers(region: Long): ServiceCall[NotUsed, List[Tracker]] = ServiceCall { _ =>
    readSideConnector.listAllTrackers(region).map(_.getOrElse(throw NotFound("No trackers found")))
  }
}
