package com.marcel.matching.api

import java.util.UUID

import akka.NotUsed
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}
import com.marcel.tracker.api.models.Tracker

trait MatchingService extends Service {

//  def matchTrip(tripId: UUID): ServiceCall[NotUsed, List[Tracker]]

  override def descriptor: Descriptor = {
    import Service._
    named("matching").withAutoAcl(true)
  }

}
