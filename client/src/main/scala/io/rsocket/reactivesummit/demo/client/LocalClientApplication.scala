package io.rsocket.reactivesummit.demo.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.rsocket.{RSocket, RSocketFactory}
import io.rsocket.reactivesummit.demo.{RankingServiceClient, RecordsServiceClient, TournamentServiceClient}
import io.rsocket.transport.akka.client.TcpClientTransport

import scala.concurrent.ExecutionContext

object LocalClientApplication extends App {

  implicit val system: ActorSystem = ActorSystem("LocalClient")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val rsocket: RSocket = RSocketFactory.connect()
    .transport(new TcpClientTransport("localhost", 7001))
    .start()
    .block()

  val recordsService = new RecordsServiceClient(rsocket)
  val rankingService = new RankingServiceClient(rsocket)
  val tournamentService = new TournamentServiceClient(rsocket)

  ClientRunner(recordsService, rankingService, tournamentService)
    .block()
}
