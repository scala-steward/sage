package sage.examples.zio

import zio.*

import sage.*
import sage.backend.*

/**
  * Classic channel pub/sub surfaced as a native `ZStream`; closing the surrounding scope unsubscribes. Sharded pub/sub
  * (`sSubscribe`/`sPublish`) is shown in the cluster spotlight, where it belongs.
  */
object PubSubExample {

  val run: ZIO[SageClient, Throwable, Unit] =
    ZIO.serviceWithZIO[SageClient] { client =>
      ZIO.scoped {
        for {
          stream   <- client.subscribeScoped[String]("news")
          _        <- ZIO.foreachDiscard(1 to 3)(i => client.publish("news", s"item-$i"))
          messages <- stream.take(3).runCollect
          _        <- Console.printLine(s"received=${messages.map(_.payload).toList}")
        } yield ()
      }
    }
}
