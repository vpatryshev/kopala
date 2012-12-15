package kopala

import transport.{ViaPipe, ViaActor}

object Konsole {
  def main(args: Array[String]) {
   val config: Map[String, String] = args.filter(_.indexOf("=") > 0).map(_ split "=").map(p => (p(0), p(1))).toMap
   if (config contains "port") ViaActor(config("port").toInt)
   else                        ViaPipe(System.in, System.out, System.err)
  }
}

