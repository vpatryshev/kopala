package kopala.transport

import java.io.FileOutputStream
import scala.actors.Actor

import scala.actors.remote.RemoteActor._
import collection.mutable.ArrayBuffer

class ViaActor(myPort: Int) extends Actor {
  val msgs = new ArrayBuffer[String]
  def log(m: Any) {
    msgs += new java.util.Date() + "] " + m
  }

  var msgno: Int = 0

  def act() {
    alive(myPort)
    register('kopala, this)
    loopWhile(true) {
      react {
        case msg => {
          log(msg)
                    reply((msgno, new java.util.Date(), msg))
          try {
            sender ! (msgno, msg, "received at", new java.util.Date)
          } catch {
            case e: Exception => println(e)
          }
        }
      }
    }
  }

}
