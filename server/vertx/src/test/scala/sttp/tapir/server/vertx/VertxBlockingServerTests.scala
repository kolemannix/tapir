package sttp.tapir.server.vertx

import io.vertx.scala.ext.web.{Route, Router}
import org.scalatest.BeforeAndAfterEach
import sttp.tapir._
import sttp.tapir.server.{DecodeFailureHandler, ServerDefaults}
import sttp.tapir.tests.PortCounter

import scala.concurrent.Future
import scala.reflect.ClassTag

class VertxBlockingServerTests extends VertxServerTests with BeforeAndAfterEach {

  override def suspendResult[T](t: => T): Future[T] = vertx.executeBlocking(() => t)

  override def route[I, E, O](e: Endpoint[I, E, O, String], fn: I => Future[Either[E, O]], decodeFailureHandler: Option[DecodeFailureHandler]): Router => Route =
    e.blockingRoute(fn, vertx)(options.copy(decodeFailureHandler.getOrElse(ServerDefaults.decodeFailureHandler)))

  override def routeRecoverErrors[I, E <: Throwable, O](e: Endpoint[I, E, O, String], fn: I => Future[O])
                                                       (implicit eClassTag: ClassTag[E]): Router => Route =
    e.blockingRouteRecoverErrors(fn, vertx)

  override lazy val portCounter: PortCounter = new PortCounter(4000)

}
