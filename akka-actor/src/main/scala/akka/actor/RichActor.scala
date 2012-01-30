package akka.actor

import akka.dispatch.{ Mailbox, DispatcherWithStash, MessageQueueWithStash }
import scala.util.continuations.{ cpsParam, shift, reset }

trait RichActor extends Actor {

  override val dispatcher =
    DispatcherWithStash.globalDispatcher

  private[this] val mailbox: MessageQueueWithStash = {
    var mbox: Mailbox = null
    try {
      mbox = context.asInstanceOf[ActorCell].mailbox
    } catch {
      case e ⇒
        throw new ActorInitializationException(self, "mailbox with stash required", e)
    } finally {
      if (mbox == null || !mbox.isInstanceOf[MessageQueueWithStash])
        throw new ActorInitializationException(self, "mailbox with stash required")
    }
    mbox.asInstanceOf[MessageQueueWithStash]
  }

  def stash(): Unit = {
    mailbox.stash(context.currentMessage)
  }

  def unstashAll(): Unit = {
    mailbox.unstashAll()
  }

}
