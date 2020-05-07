package com.marcel.trip.api.models

import play.api.libs.json.Format
import com.marcel.jsonformats.JsonFormats._

object Categories extends Enumeration {
  type Category = Value
  val LUXER,
    WOMEN,
    PET,
    LUXO = Value
  implicit val format: Format[Category] = enumFormat(Categories)
}
