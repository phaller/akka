package akka.dispatch

import java.util.Deque
import java.util.concurrent.LinkedBlockingDeque
import akka.actor.ActorCell

trait DequeBasedMessageQueue extends QueueBasedMessageQueue {
  def queue: Deque[Envelope]
}

case class UnboundedDequeBasedMailbox() extends MailboxType {
  def create(receiver: ActorCell) =
    new Mailbox(receiver) with DequeBasedMessageQueue with UnboundedMessageQueueSemantics with DefaultSystemMessageQueue {
      final val queue = new LinkedBlockingDeque[Envelope]()
    }
}
