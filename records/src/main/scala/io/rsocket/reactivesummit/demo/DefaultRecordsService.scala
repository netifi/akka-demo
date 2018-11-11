package io.rsocket.reactivesummit.demo

import io.netty.buffer.ByteBuf
import reactor.core.publisher.Flux
import slick.basic.DatabaseConfig
import slick.jdbc.{GetResult, ResultSetConcurrency, ResultSetType}

import scala.collection.JavaConverters._

class DefaultRecordsService(implicit session: DatabaseConfig[PostgresProfile]) extends RecordsService {
  import session.profile.plainAPI._

  implicit val getRecord: GetResult[Record] =
    GetResult(r => {
      val record = Record.newBuilder()
      r.nextIntOption.foreach(record.setId)
      r.nextArrayOption.foreach(values => record.addAllAliases(values.asJava))
      r.nextArrayOption.foreach(values => record.addAllAuthors(values.asJava))
      r.nextStringOption.foreach(record.setDescription)
      r.nextStringOption.foreach(record.setBackground)
      r.nextStringOption.foreach(record.setThumbnail)
      r.nextStringOption.foreach(record.setName)
      r.nextArrayOption.foreach(values => record.addAllPartners(values.asJava))
      r.nextArrayOption.foreach(values => record.addAllPowers(values.asJava))
      r.nextIntOption.foreach(record.setComicCount)
      r.nextIntOption.foreach(record.setEventCount)
      r.nextIntOption.foreach(record.setPageviewCount)
      r.nextIntOption.foreach(record.setSerieCount)
      r.nextIntOption.foreach(record.setStoryCount)
      r.nextArrayOption.foreach(values => record.addAllSecretIdentities(values.asJava))
      r.nextArrayOption.foreach(values => record.addAllSpecies(values.asJava))
      r.nextStringOption.foreach(record.setSuperName)
      r.nextArrayOption.foreach(values => record.addAllTeams(values.asJava))
      r.nextStringOption.foreach(record.setMarvelUrl)
      r.nextStringOption.foreach(record.setWikipediaUrl)
      record.build()
    })

  override def records(request: RecordsRequest, metadata: ByteBuf): Flux[Record] = {
    Flux.from(session.db.stream(
      sql"""SELECT * FROM records WHERE thumbnail is not null
            ORDER BY id OFFSET ${request.getOffset} LIMIT ${request.getMaxResults}"""
        .as[Record]
        .withStatementParameters(
          rsType = ResultSetType.ForwardOnly,
          rsConcurrency = ResultSetConcurrency.ReadOnly,
          fetchSize = 100)
        .transactionally))
  }
}
