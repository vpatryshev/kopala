package kopala

import io.Source
import java.io._

/**
 * All obvious stuff goes here
 * Uses "magnet pattern" (lmgtfy)
 */
object Obvious {
  implicit def asSource(in: InputStream): Source = Source.fromInputStream(in)
  implicit def asWriter(out: OutputStream): Writer = new OutputStreamWriter(out)
  implicit def asPrintWriter(out: Writer): PrintWriter = new PrintWriter(out)
  implicit def read(s: String): Reader = new StringReader(s)
}
