package io.rsocket.reactivesummit.demo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.netifi.broker.BrokerClient
import reactor.core.scala.publisher._
import slick.basic.DatabaseConfig

import scala.concurrent.ExecutionContext

object RecordsServiceApplication extends App {

  implicit val system: ActorSystem = ActorSystem("RecordsService")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  implicit val session: DatabaseConfig[PostgresProfile] = DatabaseConfig.forConfig("postgres")
  system.registerOnTermination(session.db.close())

  val proteus: BrokerClient = BrokerClient.tcp().build()
  val recordsService = new DefaultRecordsService()

  proteus.addService(new RecordsServiceServer(recordsService, None, None))
}
