package io.rsocket.reactivesummit.demo

import akka.stream.alpakka.slick.scaladsl.SlickSession
import io.netty.buffer.ByteBuf
import reactor.core.publisher.Flux
import slick.jdbc.GetResult

class DefaultRecordsService(implicit session: SlickSession) extends RecordsService {
  import session.profile.api._

  implicit val getRecord: GetResult[Record] =
    GetResult(r => {
      val record = Record.newBuilder()
      r.nextIntOption.foreach(record.setId)
      r.skip.skip
      r.nextStringOption.foreach(record.setDescription)
      r.nextStringOption.foreach(record.setBackground)
      r.nextStringOption.foreach(record.setThumbnail)
      r.nextStringOption.foreach(record.setName)
      r.skip.skip
      r.nextIntOption.foreach(record.setComicCount)
      r.nextIntOption.foreach(record.setEventCount)
      r.nextIntOption.foreach(record.setPageviewCount)
      r.nextIntOption.foreach(record.setSerieCount)
      r.nextIntOption.foreach(record.setStoryCount)
      r.skip.skip
      r.nextStringOption.foreach(record.setSuperName)
      r.skip
      r.nextStringOption.foreach(record.setMarvelUrl)
      r.nextStringOption.foreach(record.setWikipediaUrl)
      record.build()
    })

  override def records(request: RecordsRequest, metadata: ByteBuf): Flux[Record] = {
    Flux.from(session.db.stream(
      sql"""SELECT * FROM records WHERE thumbnail is not null
            ORDER BY id OFFSET ${request.getOffset} LIMIT ${request.getMaxResults}""".as[Record]))
  }
}
