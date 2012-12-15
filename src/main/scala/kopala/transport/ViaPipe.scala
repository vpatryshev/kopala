package kopala.transport

import io.Source
import java.io._
import scala.Left
import scala.Right

object ViaPipe extends Connector {
  def apply(in: InputStream = System.in, out: OutputStream = System.out, err: OutputStream = System.err) {
    pipe(Source.fromInputStream(in), new OutputStreamWriter(out), new OutputStreamWriter(err))
  }
}