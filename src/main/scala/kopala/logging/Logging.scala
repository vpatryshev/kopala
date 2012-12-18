package kopala.logging

trait Logger {
  def log(message: String)
}

trait SimpleLogger extends Logger {
  override def log(message: String) = println(message)
}

trait Logging {
  val logger: Logger
  def log(message: String) = Option(logger).foreach(_.log(message))
}

trait SimpleLogging extends Logging {
  val logger = new SimpleLogger{}
}