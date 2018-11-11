package io.rsocket.reactivesummit.demo

import com.github.tminglei.slickpg.{ExPostgresProfile, PgArraySupport}

trait PostgresProfile extends ExPostgresProfile with PgArraySupport {
  val plainAPI = new API with SimpleArrayPlainImplicits
}

object PostgresProfile extends PostgresProfile