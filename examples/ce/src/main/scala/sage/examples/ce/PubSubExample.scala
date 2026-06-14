package sage.examples.ce

import cats.effect.IO
import cats.syntax.all.*

import sage.*
import sage.ce.*

/**
  * Classic channel pub/sub surfaced as a native fs2 `Stream`; releasing the resource unsubscribes. Sharded pub/sub is shown in the cluster
  * spotlight, where it belongs.
  */
object PubSubExample {

  def run(client: SageClient): IO[Unit] =
    client.subscribeResource[String]("news").use { stream =>
      for {
        _        <- (1 to 3).toList.traverse_(i => client.publish("news", s"item-$i"))
        messages <- stream.take(3).compile.toVector
        _        <- IO.println(s"received=${messages.map(_.payload).toList}")
      } yield ()
    }
}
