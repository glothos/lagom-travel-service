package com.marcel.tracker.impl

import java.util.UUID

import akka.Done
import com.datastax.driver.core.PreparedStatement
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}
import com.marcel.tracker.impl.events.TrackerEvent
import com.marcel.tracker.api.models.TrackerStatuses
import com.marcel.tracker.impl.events.{PositionSynced, TrackerCreated, TrackerEvent}
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

class TrackerReadSideProcessor(readSide: CassandraReadSide, session: CassandraSession)
  (implicit ec: ExecutionContext) extends ReadSideProcessor[TrackerEvent]{

  private val log = LoggerFactory.getLogger(classOf[TrackerReadSideProcessor])

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[TrackerEvent] = {
    readSide.builder[TrackerEvent]("trackerOffset")
      .setGlobalPrepare(createTable)
      .setPrepare{_ => prepareStatements()}
      .setEventHandler[PositionSynced](positionSync)
      .setEventHandler[TrackerCreated](trackerCreation)
      .build()
  }

  override def aggregateTags: Set[AggregateEventTag[TrackerEvent]] = TrackerEvent.Tag.allTags

  private var updateTrackerStatement: PreparedStatement = _
  private var createTrackerStatement: PreparedStatement = _

  private def createTable(): Future[Done] = {
    for {
      _ <- session.executeCreateTable(
        """
          |CREATE TABLE IF NOT EXISTS trackers (
          |   user_id uuid primary key,
          |   lat double,
          |   lng double,
          |   timestamp bigint,
          |   status text,
          |   current_region bigint,
          | )
          |""".stripMargin)
    } yield Done
  }
  private def prepareStatements(): Future[Done] = {
    for {
      updateTracker <- session.prepare(
        """
          |UPDATE trackers
          | SET lat = ?, lng = ?, timestamp = ?, current_region = ?
          | WHERE user_id = ?
          |""".stripMargin)
      createTracker <- session.prepare(
        """
          |INSERT INTO trackers (lat, lng, timestamp, status, user_id, current_region)
          | VALUES (?, ?, ?, ?, ?, ?)
          |""".stripMargin)
    } yield {
      updateTrackerStatement = updateTracker
      createTrackerStatement = createTracker
      Done
    }
  }

  private def positionSync(e: EventStreamElement[PositionSynced]) = {
    log.info("ReadSideProcessor received position sync update")
    Future.successful {
      val s = e.event
      List(updateTrackerStatement.bind(
        s.lat.asInstanceOf[java.lang.Double],
        s.lng.asInstanceOf[java.lang.Double],
        s.timestamp.asInstanceOf[java.lang.Long],
        s.currentRegion.asInstanceOf[java.lang.Long],
        s.userId
      ))
    }
  }

  private def trackerCreation(e: EventStreamElement[TrackerCreated]) = {
    log.info("ReadSideProcessor received tracker creation event trigger")
    Future.successful {
      val s = e.event
      List(createTrackerStatement.bind(
        s.lat.asInstanceOf[java.lang.Double],
        s.lng.asInstanceOf[java.lang.Double],
        System.currentTimeMillis().asInstanceOf[java.lang.Long],
        TrackerStatuses.AVAILABLE.toString,
        s.userId,
        s.currentRegion.asInstanceOf[java.lang.Long],
      ))
    }
  }
}
