package kopala.transport

import java.io.{OutputStream, InputStream}
import kopala.Obvious._
import kopala.logging.SimpleLogging

object ViaPipe extends SimpleLogging with Connector {
  def apply(in: InputStream, out: OutputStream, err: OutputStream): Unit = pipe(in, out, err, ">")
  def apply(in: InputStream = System.in, out: OutputStream = System.out): Unit = apply(in, out, out)
}