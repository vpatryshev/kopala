package kopala.transport

import java.io.{OutputStream, InputStream}
import kopala.Obvious._
import kopala.logging.SimpleLogging

object ViaPipe extends Connector with SimpleLogging {
  def apply(in: InputStream, out: OutputStream, err: OutputStream): Unit = pipe(in, out, err, ">")
  def apply(in: InputStream = System.in, out: OutputStream = System.out): Unit = apply(in, out, out)
}