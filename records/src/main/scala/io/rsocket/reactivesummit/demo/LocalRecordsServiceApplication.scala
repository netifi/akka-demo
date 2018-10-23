package io.rsocket.reactivesummit.demo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.slick.javadsl.SlickSession
import io.rsocket.RSocketFactory
import io.rsocket.transport.akka.server.TcpServerTransport
import reactor.core.scala.publisher._
import reactor.core.publisher.Mono

import scala.concurrent.ExecutionContext

object LocalRecordsServiceApplication extends App {

  implicit val system: ActorSystem = ActorSystem("LocalRecordsService")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  implicit val session: SlickSession = SlickSession.forConfig("slick-postgres")
  system.registerOnTermination(session.close())

  val recordsService = new DefaultRecordsService()
  val recordsServiceServer = new RecordsServiceServer(recordsService, None, None)

  val server = RSocketFactory.receive()
      .acceptor((setup, sendingSocket) => Mono.just(recordsServiceServer))
      .transport(new TcpServerTransport("localhost", 7001))
      .start()
      .block()
}
