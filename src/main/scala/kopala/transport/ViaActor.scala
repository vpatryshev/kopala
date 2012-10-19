package kopala.transport

import scala.actors.Actor

import scala.actors.remote.RemoteActor._
import kopala.js.Kopala
import java.io.{PrintWriter, StringWriter}

class ViaActor(myPort: Int) extends Actor {
  val kopala = new Kopala
  def log(x: Any) { println(x) }

  def log(msg: String, e: Exception) {
    val stack = new StringWriter
    e.printStackTrace(new PrintWriter(stack))
    log(msg + " " + e + "\n" + stack)
  }

  def act() {
    alive(myPort)
    register('kopala, this)

    loopWhile(true) {
      react { case msg: String => reply(exec(msg)) }
    }
  }

  def exec(msg: String) = {
    val out = new StringWriter()
    try {
      log("Got message " + msg)
      val result = kopala(msg, out)
      log("got result " + result)
      val dump = out.toString
      log("got dump " + dump)
      Right(result, dump)
    } catch {
      case e: Exception => {
        log("oops,", e)
        Left(e, out.toString)
      }
    }
  }
}
