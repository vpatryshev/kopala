package kopala.logging

trait Logger {
  def log(message: String)
}

trait SimpleLogger extends Logger {
  override def log(message: String) = println(message)
}

object STDOUT extends SimpleLogger{}

trait Logging {
  val logger: Logger

  def log(message: String) = {
    try {
      logger.log(message)
    } catch {
      case x => STDOUT.log(message)
    }
  }
}

trait SimpleLogging extends Logging {
  val logger = STDOUT
}