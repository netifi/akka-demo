package io.rsocket.reactivesummit.demo

import io.netty.buffer.ByteBuf
import reactor.core.publisher.Mono
import scala.collection.JavaConverters._

object DefaultRankingService extends RankingService {
  override def rank(request: RankingRequest, metadata: ByteBuf): Mono[RankingResponse] = {
    val record = request.getRecordsList.asScala.maxBy(_.getStoryCount)
    Mono.just(RankingResponse.newBuilder().setId(record.getId).build())
  }
}
