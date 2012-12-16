package kopala.logging

trait Logging { def log(message: String) }

trait SimpleLogging extends Logging {
  override def log(message: String) = println(message)
}