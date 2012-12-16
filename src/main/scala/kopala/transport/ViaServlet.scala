package kopala.transport

import kopala.Obvious._

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import kopala.logging.SimpleLogging

class ViaServlet extends HttpServlet with Connector {
  override def doPost(req : HttpServletRequest, res : HttpServletResponse) {
    res.setContentType("text/html")
    pipe(req, res)
  }
}
