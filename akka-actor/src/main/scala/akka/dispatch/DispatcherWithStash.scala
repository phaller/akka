package akka.dispatch

import akka.actor.ActorSystem

object DispatcherWithStash {

  lazy val globalDispatcher: MessageDispatcher = {
    val sys = ActorSystem()
    sys.dispatcherFactory.newDispatcher("dispatcher-with-mailbox-with-stash", sys.settings.DispatcherThroughput, UnboundedMailboxWithStash()).build
  }

}
