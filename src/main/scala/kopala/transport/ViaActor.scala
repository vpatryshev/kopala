package kopala.transport

import scala.actors.Actor

import scala.actors.remote.RemoteActor._
import kopala.js.Kopala
import java.io.{PrintWriter, StringWriter}
import org.mozilla.javascript.EcmaError
import kopala.logging.SimpleLogging

class ViaActor(myPort: Int = 8722) extends Connector with Actor with SimpleLogging {

  def act() {
    alive(myPort)
    register('kopala, this)

    loopWhile(true) {
      react { case msg: String => { println("<<< " + msg); reply(exec(msg))} }
    }
  }
}

object ViaActor {
  def apply(port: Int = 8722) = {
    val actor = new ViaActor(port)
    actor.start()
    actor
  }
}