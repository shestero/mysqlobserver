import akka.http.scaladsl.common.EntityStreamingSupport

import scala.jdk.CollectionConverters._
import io.r2dbc.client.R2dbc
import dev.miku.r2dbc.mysql._

//import monix.reactive.Observable
//import monix.execution.Scheduler.Implicits.global
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.Try
import reactor.core.publisher.Flux

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
//import akka.http.scaladsl.model.ContentType
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.io.StdIn


object Main extends App {
  val app = "mysqlobserver"

  println(s"HELLO\t$app")

  implicit val system = ActorSystem(Behaviors.empty, app)
  //implicit val ec = system.dispatchers.lookup()
  //implicit val materializer = ActorMaterializer()

  val config = MySqlConnectionConfiguration
    .builder()
    .host("localhost")
    .database("uzg31det")
    .username("shestero")
    .password("")
    .build()

  val r2dbc = new R2dbc(MySqlConnectionFactory.from(config))

  val route =
    path("tsv") {
      get {
        val result : Flux[String] = r2dbc.withHandle { handle =>
          handle.select("SELECT /* ROW_NUMBER(), */ * FROM g31det LIMIT 1111").mapRow { row =>
            //val meta = row.getMetadata.getColumnMetadatas.asScala.map(col=>col.getName).toList
            //meta.map(row.get).map(_.toString)
            LazyList.from(0).map(num=>Try{row.get(num).toString}).takeWhile(_.isSuccess).flatten(_.get)
              .mkString("\t")
          }
        }
        println("got result")

        // simple example to run with monix-reactive
        //val observable = Observable.fromReactivePublisher(result)
        //println("got observable")

        val source = Source.fromPublisher(result) // (observable.toReactivePublisher)
        val byteStringSource = source.map(_+"\r\n").map(ByteString(_))

        complete( HttpEntity(MediaTypes.`text/tab-separated-values`
          withCharset HttpCharsets.`UTF-8`, byteStringSource) )
      }
    }

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

  println(s"Server now online. Please navigate to http://localhost:8080/hello\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

  println(s"BYE\t$app")
}
