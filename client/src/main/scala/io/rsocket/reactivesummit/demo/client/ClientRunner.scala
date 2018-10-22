package io.rsocket.reactivesummit.demo.client

import com.google.protobuf.util.JsonFormat
import io.rsocket.reactivesummit.demo._
import org.apache.logging.log4j.LogManager
import reactor.core.publisher.Mono

object ClientRunner {

  private val logger = LogManager.getLogger(getClass.getName)

  def apply(recordsService: RecordsServiceClient,
            rankingService: RankingServiceClient,
            tournamentService: TournamentServiceClient): Mono[Void] = {

    val request = RecordsRequest.newBuilder().setMaxResults(256).build()
    tournamentService.tournament(request)
      .doOnNext(result => logger.info(JsonFormat.printer().print(result)))
      .last()
      .doOnSuccess(result =>
        logger.info(
          "\n=----------------------------------------------------------=" +
          "\n< @_@ > SUPER WINNER < @_@ >  ===> " + result.getWinner.getSuperName +
          "\n=----------------------------------------------------------="))
      .then()

    /*
    val request = RecordsRequest.newBuilder().setMaxResults(40).build()
    recordsService.records(request)
        .doOnNext(record => logger.info(JsonFormat.printer().print(record)))
        .then()
    */

    /*
    val request = RankingRequest.newBuilder()
      .addRecords(Record.newBuilder()
        .setId(0)
        .setName("Ryland Degnan")
        .setDescription("Co-founder and CTO, Netifi")
        .setThumbnail("https://attendease-event-content.s3.us-west-2.amazonaws.com/events/521abb61-6216-487e-a721-db53fa7003ac/upload/content/f5060af654de642b167c.jpg"))
      .addRecords(Record.newBuilder
        .setId(1)
        .setName("Stephane Maldini")
        .setDescription("Reactive Engineering Cook, Pivotal")
        .setThumbnail("https://attendease-event-content.s3.us-west-2.amazonaws.com/events/521abb61-6216-487e-a721-db53fa7003ac/upload/content/aa207287b449bdd02dee.jpg"))
      .build()
    rankingService.rank(request)
      .then()
    */
  }
}
