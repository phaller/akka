package akka.dispatch

trait MessageQueueWithStash {
  self: DequeBasedMessageQueue ⇒

  private var stash = Vector.empty[Envelope]

  @inline
  final def stash(handle: Envelope): Unit = stash :+= handle

  @inline
  final def unstashAll(): Unit = stash.reverseIterator foreach self.queue.addFirst
}
