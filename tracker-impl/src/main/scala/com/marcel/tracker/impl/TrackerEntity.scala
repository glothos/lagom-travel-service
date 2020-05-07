package com.marcel.tracker.impl

import java.util.UUID

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import com.lightbend.lagom.scaladsl.pubsub.{PubSubRegistry, TopicId}
import com.marcel.tracker.impl.commands.GetTracker
import com.marcel.tracker.impl.events.TrackerEvent
import com.marcel.tracker.api.models.Tracker
import com.marcel.tracker.impl.aggregates.TrackerAggregate
import com.marcel.tracker.impl.commands.{SyncTracker, TrackerCommand}
import com.marcel.tracker.impl.events.{PositionSynced, TrackerCreated, TrackerEvent}
import com.uber.h3core.H3Core
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class TrackerEntity(pubSubRegistry: PubSubRegistry) extends PersistentEntity {

  override type Command = TrackerCommand[_]
  override type Event = TrackerEvent

  override type State = Option[TrackerAggregate]

  override def initialState: Option[TrackerAggregate] = None

  private val posUpdatedTopic = pubSubRegistry.refFor(TopicId[PositionSynced])
  private val log = LoggerFactory.getLogger(classOf[TrackerEntity])
  private val h3Instance = H3Core.newInstance()

  override def behavior: Behavior = {
    case Some(TrackerAggregate(_, _, _, _, _, _, _)) => syncPosition
    case None => createTracker
  }


  private def getRegion(lat: Double, lng: Double, cr: Long) = {
    val newCr = h3Instance.geoToH3(lat, lng, 5)
    if (cr == newCr) {
      cr
    } else newCr
  }

  private def createTracker: Actions =
    Actions()
    .onCommand[SyncTracker, Done] {
      case (SyncTracker(userId, lat, lng, _), ctx, _) =>
        log.info(s"$userId doesn't have a tracker. Creating...")
        val cr = h3Instance.geoToH3(lat, lng, 4)
        ctx.thenPersist(TrackerCreated(userId, lat, lng, cr))(_ => ctx.reply(Done))
    }.onEvent{
      case (TrackerCreated(userId, lat, lng, cr), _) =>
        val h3 = h3Instance.geoToH3(lat, lng, 7)
        val kRing = h3Instance.kRing(h3, 1).asScala.toList.map(_.toLong)
        Some(
          TrackerAggregate(
            userId,
            lat,
            lng,
            Some(h3),
            Some(kRing),
            System.currentTimeMillis(),
            cr
          )
        )
    }
  private def syncPosition: Actions =
    Actions()
    .onCommand[SyncTracker, Done] {
      case (SyncTracker(userId, lat, lng, tz), ctx, state @ Some(_)) =>
        log.info(s"$userId is updating tracker to $lat $lng")
        state match {
          case Some(TrackerAggregate(_, _, _, _, _, _, cr)) =>
            ctx.thenPersist(PositionSynced(userId, lat, lng, tz, getRegion(lat, lng, cr))){e =>
              ctx.reply(Done)
              posUpdatedTopic.publish(e)
            }
        }
    }.onEvent {
      case (PositionSynced(uid, latitude, longitude, tz, cr), state @ Some(_)) =>
        val h3 = h3Instance.geoToH3(latitude, longitude, 7)
        val kRing = h3Instance.kRing(h3, 1).asScala.toList.map(_.toLong)
        state.map {_.copy(
          userId = uid,
          lat = latitude,
          lng = longitude,
          h37 = Some(h3),
          kRing7 = Some(kRing),
          timestamp = tz,
        )}
    }
}
