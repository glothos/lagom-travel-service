package com.marcel.trip.impl.commands

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType

trait TripCommand[R] extends ReplyType[R]
