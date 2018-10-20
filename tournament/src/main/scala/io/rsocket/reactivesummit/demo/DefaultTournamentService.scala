package io.rsocket.reactivesummit.demo

import java.math.RoundingMode
import java.time.Duration

import com.google.common.math.IntMath
import io.netty.buffer.ByteBuf
import reactor.core.publisher.Flux

class DefaultTournamentService(val recordsService: RecordsServiceClient, val rankingService: RankingServiceClient) extends TournamentService {
  private val WINDOW_SIZE: Int = 2
  private val CONCURRENCY: Int = 4

  override def tournament(request: RecordsRequest, metadata: ByteBuf): Flux[RoundResult] = {
    tournament(recordsService.records(request), IntMath.log2(request.getMaxResults, RoundingMode.UP))
  }

  private def tournament(records: Flux[Record], i: Int): Flux[RoundResult] = {
    val winners = round(records).publish()
    val roundResult: Flux[RoundResult] = winners.map(winner => RoundResult.newBuilder.setRound(i).setWinner(winner).build)
    val tournamentResult = if (i > 1) roundResult.mergeWith(tournament(winners, i - 1)) else roundResult
    winners.connect()
    tournamentResult
  }

  private def round(records: Flux[Record]): Flux[Record] = {
    records
      .window(WINDOW_SIZE)
      .flatMap((window: Flux[Record]) => {
        window
          .collectMap[Int](key => key.getId)
          .flatMap(map => {
            if (map.size > 1) {
              rankingService
                .rank(RankingRequest.newBuilder.addAllRecords(map.values).build)
                .timeout(Duration.ofSeconds(8))
                .retry()
                .map(response => map.get(response.getId))
            } else {
              Flux.fromIterable(map.values).next
            }
          })
      }, CONCURRENCY)
  }
}
