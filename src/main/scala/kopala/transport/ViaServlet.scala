package kopala.transport

import kopala.Obvious._

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import io.Source
import java.io.Writer
import kopala.Obvious._

class ViaServlet extends HttpServlet with Connector {

  import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
  implicit def rq2source(in: HttpServletRequest): Source = in.getInputStream
  implicit def respWriter(out: HttpServletResponse): Writer = out.getOutputStream

  override def doPost(req : HttpServletRequest, res : HttpServletResponse) {
    res.setContentType("text/html")
    pipe(req, res)
  }
}
