package com.marcel.tracker.impl.commands

import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType

trait TrackerCommand[R] extends ReplyType[R]
