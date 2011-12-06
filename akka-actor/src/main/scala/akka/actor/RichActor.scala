package akka.actor

import akka.dispatch.{ DispatcherWithStash }
import scala.util.continuations.{ cpsParam, shift, reset }

trait RichActor extends Actor {

  override val dispatcher = DispatcherWithStash.globalDispatcher

}
