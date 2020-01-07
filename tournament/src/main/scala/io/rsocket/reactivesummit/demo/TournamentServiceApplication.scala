package io.rsocket.reactivesummit.demo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.netifi.broker.BrokerClient
import reactor.core.scala.publisher._

import scala.concurrent.ExecutionContext

object TournamentServiceApplication extends App {

  implicit val system: ActorSystem = ActorSystem("TournamentService")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val client: BrokerClient = BrokerClient.tcp().build()

  val recordsService = new RecordsServiceClient(client.group("reactivesummit.demo.records"))
  val rankingService = new RankingServiceClient(client.group("reactivesummit.demo.ranking"))
  val tournamentService = new DefaultTournamentService(recordsService, rankingService)

  client.addService(new TournamentServiceServer(tournamentService, None, None))
}
