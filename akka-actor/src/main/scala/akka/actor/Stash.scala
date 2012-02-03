package akka.actor

import akka.dispatch.{ Envelope, DequeBasedMessageQueue }

trait Stash {
  _: Actor ⇒

  var theStash = Vector.empty[Envelope]

  private[this] val mailbox: DequeBasedMessageQueue = {
    context.asInstanceOf[ActorCell].mailbox match {
      case queue: DequeBasedMessageQueue ⇒ queue
      case other                         ⇒ throw new ActorInitializationException(self, "mailbox with stash required, got: " + other.getClass())
    }
  }

  def stash(): Unit = theStash :+= context.currentMessage

  def unstashAll(): Unit = theStash.reverseIterator foreach mailbox.queue.addFirst

}
