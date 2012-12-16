package kopala.transport

import io.Source
import java.io.{OutputStreamWriter, OutputStream, InputStream}
import kopala.Obvious._

object ViaPipe extends Connector {
  def apply(in: InputStream = System.in, out: OutputStream = System.out, err: OutputStream = System.err) {
    pipe(in, out, err, ">")
  }
}