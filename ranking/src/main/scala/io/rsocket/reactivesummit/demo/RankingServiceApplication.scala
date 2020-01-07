package io.rsocket.reactivesummit.demo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.netifi.broker.BrokerClient
import reactor.core.scala.publisher._

import scala.concurrent.ExecutionContext

object RankingServiceApplication extends App {

  implicit val system: ActorSystem = ActorSystem("RankingService")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val client: BrokerClient = BrokerClient.tcp().build()
  val rankingService = new DefaultRankingService()

  client.addService(new RankingServiceServer(rankingService, None, None))
}
