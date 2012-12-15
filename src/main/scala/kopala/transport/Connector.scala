package kopala.transport

import kopala.js.Kopala
import java.io._
import org.mozilla.javascript.EcmaError
import io.Source
import scala.Left
import scala.Right

trait Connector {
  val kopala = new Kopala{}
  def log(x: Any) { println(x) }

  def log(msg: String, e: Exception) {
    val stack = new StringWriter
    e.printStackTrace(new PrintWriter(stack))
    log(msg + " " + e + "\n" + stack)
  }

  def exec(msg: String) = {
    val out = new StringWriter()
    try {
      log("Got message " + msg)
      val result = kopala(msg, out)
      log("got result (" + result.getClass + "): " + result)
      val dump = out.toString
      //      log("got dump " + dump)
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

  def pipe(in: Source, out: Writer, err: Writer) {
    val p = new PrintWriter(out)
    val e = new PrintWriter(err)
    in.getLines.zipWithIndex foreach { case (line, number) =>
      exec(line) match {
        case Left((errorMsg, dump)) => {p.println(dump); e.println("Line " + number + ":" + errorMsg) }
        case Right((result,  dump)) => {p.println(dump); p.println(result) }
      }
      p.print(">")
    }
  }

  def pipe(in: InputStream, out: OutputStream, err: OutputStream) {
    pipe(Source.fromInputStream(in), new OutputStreamWriter(out), new OutputStreamWriter(err))
  }

}