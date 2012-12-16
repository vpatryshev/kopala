package kopala

import io.Source
import java.io._
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

/**
 * All obvious stuff goes here
 * Uses "magnet pattern" (lmgtfy)
 */
object Obvious {
  implicit def asSource(in: InputStream): Source = Source.fromInputStream(in)
  implicit def asSource(in: HttpServletRequest): Source = asSource(in.getInputStream)
  implicit def asWriter(out: OutputStream): Writer = new OutputStreamWriter(out)
  implicit def asWriter(out: HttpServletResponse): Writer = asWriter(out.getOutputStream)
  implicit def asPrintWriter(out: Writer): PrintWriter = new PrintWriter(out)
  implicit def read(s: String): Reader = new StringReader(s)
}
