package akka.dispatch

import akka.actor.ActorSystem

object DequeBasedDispatcher {

  lazy val globalDispatcher: MessageDispatcher =
    apply()

  def apply(): MessageDispatcher = {
    val sys = ActorSystem()
    sys.dispatcherFactory.newDispatcher("dispatcher-with-deque-based-mailbox", sys.settings.DispatcherThroughput, UnboundedDequeBasedMailbox()).build
  }

}
