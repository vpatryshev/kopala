package kopala

import javax.servlet.http._
import java.io.{Serializable, ByteArrayOutputStream, PrintWriter}
import scala.collection.JavaConversions._
import transport.ViaActor

class Sample extends HttpServlet {
  val st = java.util.Arrays.asList(Thread.currentThread.getStackTrace)

  var processor: (Any => String => String) = null
  println(Sample.actor())

  def asStr(x: Any) = x match {
    case arr: Array[String] => arr.mkString
    case _                  => x.toString
  }

  override def doGet(req : HttpServletRequest, res : HttpServletResponse) {
    res.setContentType("text/html")
    val out:PrintWriter = res.getWriter
    if (processor == null) {
      out.print("Hello!<br/>" + mapAsScalaMap(req.getParameterMap).mapValues(asStr(_)))
      out.print("<br/>" + this)
      out.print("<br/>" + getClass.getClassLoader)
      out.print("<br/>" + st)

    } else {
      out.print(processor(mapAsScalaMap(req.getParameterMap)))
    }
  }
}

object Sample {
  def actor(port: Int = 8722) = new ViaActor(port)
  actor().start()
}