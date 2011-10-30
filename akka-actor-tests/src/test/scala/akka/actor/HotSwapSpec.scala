/**
 * Copyright (C) 2009-2011 Typesafe Inc. <http://www.typesafe.com>
 */

package akka.actor

import akka.testkit._

import scala.util.continuations._

@org.junit.runner.RunWith(classOf[org.scalatest.junit.JUnitRunner])
class HotSwapSpec extends AkkaSpec {

  "An Actor" must {

    "be able to hotswap its behavior with HotSwap(..)" in {
      val barrier = TestBarrier(2)
      @volatile
      var _log = ""
      val a = actorOf(new Actor {
        def receive = { case _ ⇒ _log += "default" }
      })
      a ! HotSwap(self ⇒ {
        case _ ⇒
          _log += "swapped"
          barrier.await
      })
      a ! "swapped"
      barrier.await
      _log must be("swapped")
    }

    "be able to hotswap its behavior with become(..)" in {
      val barrier = TestBarrier(2)
      @volatile
      var _log = ""
      val a = actorOf(new Actor {
        def receive = {
          case "init" ⇒
            _log += "init"
            barrier.await
          case "swap" ⇒ become({
            case _ ⇒
              _log += "swapped"
              barrier.await
          })
        }
      })

      a ! "init"
      barrier.await
      _log must be("init")

      barrier.reset
      _log = ""
      a ! "swap"
      a ! "swapped"
      barrier.await
      _log must be("swapped")
    }

    "be able to hotswap its behavior with react" in {
      val barrier = TestBarrier(2)
      @volatile
      var _log = ""
      val a = actorOf(new Actor {
        def receive = {
          case "init" ⇒
            _log += "init"
            barrier.await
          case "react" ⇒
            reset {
              react {
                case "accept" ⇒
                  unstashAll()
                  _log += "accepted"
                case _ ⇒
                  stash()
                  _log += "stashed"
                  barrier.await
              }
            }
          case "stashed" ⇒
            _log += "reset"
            barrier.await
        }
      })

      a ! "init"
      barrier.await
      _log must be("init")

      barrier.reset
      _log = ""
      a ! "react"
      a ! "stashed"
      barrier.await
      _log must be("stashed")

      barrier.reset
      _log = ""
      a ! "accept"

      barrier.await
      _log must be("acceptedreset")
    }

    "be able to revert hotswap its behavior with RevertHotSwap(..)" in {
      val barrier = TestBarrier(2)
      @volatile
      var _log = ""
      val a = actorOf(new Actor {
        def receive = {
          case "init" ⇒
            _log += "init"
            barrier.await
        }
      })

      a ! "init"
      barrier.await
      _log must be("init")

      barrier.reset
      _log = ""
      a ! HotSwap(self ⇒ {
        case "swapped" ⇒
          _log += "swapped"
          barrier.await
      })

      a ! "swapped"
      barrier.await
      _log must be("swapped")

      barrier.reset
      _log = ""
      a ! RevertHotSwap

      a ! "init"
      barrier.await
      _log must be("init")

      // try to revert hotswap below the bottom of the stack
      barrier.reset
      _log = ""
      a ! RevertHotSwap

      a ! "init"
      barrier.await
      _log must be("init")
    }

    "be able to revert hotswap its behavior with unbecome" in {
      val barrier = TestBarrier(2)
      @volatile
      var _log = ""
      val a = actorOf(new Actor {
        def receive = {
          case "init" ⇒
            _log += "init"
            barrier.await
          case "swap" ⇒
            become({
              case "swapped" ⇒
                _log += "swapped"
                barrier.await
              case "revert" ⇒
                unbecome()
            })
            barrier.await
        }
      })

      a ! "init"
      barrier.await
      _log must be("init")

      barrier.reset
      _log = ""
      a ! "swap"
      barrier.await

      barrier.reset
      _log = ""
      a ! "swapped"
      barrier.await
      _log must be("swapped")

      barrier.reset
      _log = ""
      a ! "revert"
      a ! "init"
      barrier.await
      _log must be("init")
    }
  }
}
