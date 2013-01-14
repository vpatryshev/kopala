package kopala

import javax.servlet.http._
import java.io.PrintWriter
import scala.collection.JavaConversions._
import transport.ViaActor
import javax.servlet.ServletConfig

class Sample extends HttpServlet {
  val st = Thread.currentThread.getStackTrace

  {
    def actor(port: Int = 8722) = new ViaActor(port)
    try {
      actor().start()
    } catch {
      case x => println("Oh shit: " + x + " - while starting the plugin")
    }
  }

  var processor: (Any => String => String) = null

  def asStr(x: Any) = x match {
    case arr: Array[String] => arr.mkString
    case _                  => x.toString
  }

  case class Response(res: HttpServletResponse) {
    val out:PrintWriter = res.getWriter
    def << (x: Any) = { out print x; this }
  }

  override def doGet(req : HttpServletRequest, res : HttpServletResponse) {

    res.setContentType("text/html")
    val out = Response(res)
    if (processor == null) {
      out << "Hello!<br/>" << mapAsScalaMap(req.getParameterMap).mapValues(asStr(_))
      out << "<br/><br/><code>this = " << this
      out << "<br/><br/>" << getClass.getClassLoader
      out << "<br/><br/>" << Thread.currentThread.getId
      out << "<br/><br/>st=" << st
      out << "<br/><ol><li>" << (st.toList mkString("</li><li>"))
      out << "</li></ol></code></br>"
      out << getServletConfig.getServletContext//.addServlet
    } else {
      out << processor(mapAsScalaMap(req.getParameterMap))
    }
  }
}
