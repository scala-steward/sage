package sage.examples.kyo

import kyo.*

import sage.*
import sage.kyo.*

/**
  * Classic channel pub/sub surfaced as a native Kyo `Stream`. Sharded pub/sub is shown in the cluster spotlight, where it belongs.
  */
object PubSubExample {

  def run(client: SageClient): Unit < (Scope & Abort[Throwable] & Async) =
    for {
      stream <- client.subscribeScoped[String]("news")
      _      <- Kyo.foreachDiscard(1 to 3)(i => client.publish("news", s"item-$i"))
      chunk  <- stream.take(3).run
      _      <- Console.printLine(s"received=${chunk.toList.map(_.payload)}")
    } yield ()
}
