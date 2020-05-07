package com.marcel.tracker.impl

import java.util.UUID

import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.marcel.tracker.api.models.Tracker
import com.marcel.tracker.api.models.TrackerStatuses.TrackerStatus

import scala.concurrent.{ExecutionContext, Future}

class TrackerReadSideConnector(dbSession: CassandraSession)(implicit ec: ExecutionContext) {
  def listAllTrackers(region: Long): Future[Option[List[Tracker]]] = {
    dbSession.selectAll(
      s"""
         |SELECT * FROM trackers WHERE current_region = $region ALLOW FILTERING;
         |""".stripMargin
    ).map(_.headOption.map(row => List(Tracker(
      row.getUUID("user_id"),
      row.getDouble("lat"),
      row.getDouble("lng"),
      row.getLong("timestamp"),
      Some(row.getLong("current_region")),
      row.getString("status").asInstanceOf[TrackerStatus]
    ))))
  }
}
