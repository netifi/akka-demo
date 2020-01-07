package io.rsocket.reactivesummit.demo.client

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.netifi.broker.BrokerClient
import io.rsocket.reactivesummit.demo.{RankingServiceClient, RecordsServiceClient, TournamentServiceClient}

import scala.concurrent.ExecutionContext

object ClientApplication extends App {

  implicit val system: ActorSystem = ActorSystem("Client")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val client: BrokerClient = BrokerClient.tcp().build()
  val recordsService = new RecordsServiceClient(client.group("reactivesummit.demo.records"))
  val rankingService = new RankingServiceClient(client.group("reactivesummit.demo.ranking"))
  val tournamentService = new TournamentServiceClient(client.group("reactivesummit.demo.tournament"))

  ClientRunner(recordsService, rankingService, tournamentService)
    .block()
}
