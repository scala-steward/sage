package sage.examples.ox

import ox.Ox

import sage.*
import sage.ox.*

/**
  * Classic channel pub/sub surfaced as a native Ox `Flow`; ending the flow unsubscribes. Sharded pub/sub is shown in the cluster spotlight,
  * where it belongs.
  */
object PubSubExample {

  def run(client: SageClient)(using Ox): Unit = {
    val news     = client.subscribe[String]("news")
    (1 to 3).foreach { i =>
      val _ = client.publish("news", s"item-$i")
    }
    val messages = news.take(3).runToList()
    println(s"received=${messages.map(_.payload)}")
  }
}
