package io.rsocket.reactivesummit.demo

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config._
import io.netifi.proteus.Proteus
import io.rsocket.transport.akka.client.TcpClientTransport
import reactor.core.scala.publisher._

import scala.concurrent.ExecutionContext

object TournamentServiceApplication extends App {

  implicit val system: ActorSystem = ActorSystem("TournamentService")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val conf: Config = ConfigFactory.load()
  val proteus: Proteus = Proteus.builder()
    .sslDisabled(conf.getBoolean("netifi.proteus.ssl.disabled"))
    .accessKey(conf.getLong("netifi.proteus.access.key"))
    .accessToken(conf.getString("netifi.proteus.access.token"))
    .group(conf.getString("netifi.proteus.group"))
    .host(conf.getString("netifi.proteus.broker.hostname"))
    .port(conf.getInt("netifi.proteus.broker.port"))
    .clientTransportFactory(address => {
      val inetAddress = address.asInstanceOf[InetSocketAddress]
      new TcpClientTransport(inetAddress.getHostName, inetAddress.getPort)
    })
    .build()

  val recordsService = new RecordsServiceClient(proteus.group("reactivesummit.demo.records"))
  val rankingService = new RankingServiceClient(proteus.group("reactivesummit.demo.ranking"))
  val tournamentService = new DefaultTournamentService(recordsService, rankingService)

  proteus.addService(new TournamentServiceServer(tournamentService, None, None))
}
