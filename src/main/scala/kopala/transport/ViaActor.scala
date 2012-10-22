package kopala.transport

import scala.actors.Actor

import scala.actors.remote.RemoteActor._
import kopala.js.Kopala
import java.io.{PrintWriter, StringWriter}
import org.mozilla.javascript.EcmaError

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
      react { case msg: String => { println("Got a " + msg); reply(exec(msg))} }
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
      case ee: EcmaError => {
        log("oops: " + ee.getErrorMessage)
        Left(ee.getErrorMessage, out.toString)
      }
      case e: Exception => {
        log("oops,", e)
        Left(e.getMessage, out.toString)
      }
    }
  }
}
