package akka.dispatch

import java.util.{ ArrayDeque, Deque }
import java.util.concurrent.LinkedBlockingDeque
import akka.actor.ActorCell

trait DequeBasedMessageQueue extends QueueBasedMessageQueue {
  def queue: Deque[Envelope]
}

trait MessageQueueWithStash {
  self: DequeBasedMessageQueue ⇒

  private val stash = new ArrayDeque[Envelope]

  @inline
  final def stash(handle: Envelope): Unit =
    stash addLast handle

  @inline
  final def unstashAll(): Unit = {
    for (_ ← 0 until stash.size) {
      self.queue.addFirst(stash.removeLast())
    }
  }
}

case class UnboundedMailboxWithStash() extends MailboxType {
  def create(receiver: ActorCell) =
    new Mailbox(receiver) with DequeBasedMessageQueue with UnboundedMessageQueueSemantics with DefaultSystemMessageQueue with MessageQueueWithStash {
      final val queue = new LinkedBlockingDeque[Envelope]()
    }
}
