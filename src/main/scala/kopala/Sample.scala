package kopala

import javax.servlet.http._
import java.io.PrintWriter
import scala.collection.JavaConversions._
import javax.management.remote.rmi._RMIConnection_Stub
import transport.ViaActor

class Sample extends HttpServlet {
  var processor: (Any => String => String) = null

  def asStr(x: Any) = x match {
    case arr: Array[String] => arr.mkString
    case _                  => x.toString
  }

  override def doGet(req : HttpServletRequest, res : HttpServletResponse) : Unit = {
    res.setContentType("text/html")
    val out:PrintWriter = res.getWriter()
    if (processor == null) {
      out.print("Hello!<br/>" + mapAsScalaMap(req.getParameterMap).mapValues(asStr(_)))
      out.print("<hr>")
      out.print(Sample.messages)
    } else {
      out.print(processor(mapAsScalaMap(req.getParameterMap)))
    }
  }
}

object Sample {
  val actor = new ViaActor(8721)
  actor.start()
  def messages = actor.msgs.mkString("<br/>")
}