package kopala.transport

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

class ViaServlet extends HttpServlet with Connector {
  override def doPost(req : HttpServletRequest, res : HttpServletResponse) {
    res.setContentType("text/html")
    val out = res.getOutputStream
    pipe(req.getInputStream, out, out)
  }
}
