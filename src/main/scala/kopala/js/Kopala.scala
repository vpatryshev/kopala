package kopala.js

import scala.collection.JavaConversions._
import scalaz._
import Scalaz._
import org.mozilla.javascript._
import java.io.{FileReader,InputStreamReader,Reader,Writer}
import collection.immutable.Map
import kopala.js.Adapter._
import kopala.Obvious._
import kopala.logging.Logging
import java.lang.reflect.Method

trait Kopala extends ImporterTopLevel with Logging {
  val x = new java.util.Date |> (x => "[" + x + "]")
  val cf = new ContextFactory {
      override protected def makeContext(): Context = {
        val context = super.makeContext
        context.setLanguageVersion(Context.VERSION_1_8)
        context
      }
    }

  val context: Context = cf.enterContext
  val scope = context.initStandardObjects(this, false)

  def methodName(m: Method) = {
    val name = m.getName
    (m.getDeclaredAnnotations collect {case export: Export => export.name} filter (!_.isEmpty)).headOption getOrElse name
  }

  def names: Array[String] = {
    getClass.getDeclaredMethods filter {
      _.getDeclaredAnnotations exists {_.isInstanceOf[Export]}
    } map { methodName } toArray
  }

  try {
    scope.defineFunctionProperties(names, getClass, ScriptableObject.DONTENUM)
  } catch {
    case x: Exception => x.printStackTrace(); System.exit(1)
  }

//  def smartMap(obj: NativeObject) = mapAsScalaMap(obj.asInstanceOf[java.util.Map[Any, Any]])

  def fail(msg: String) = { throw new IllegalStateException(msg) }

//  def scope(key: String): Any = scope get key

  @Export def pause(x: AnyRef) { // good for debugging
    print("Pause @", x, "\n")
  }

  @Export def function1(fun: Function) = (x: AnyRef) => fun.call(context, scope, null, Array(x))

  @Export def asString(x: Any): String = {
//    println("asString(" + Option(x).map(_.getClass) + ":" + x + ")")
    x match {
      case a: Array[_]                  => a map asString toString
      case javaCollection: java.util.Collection[_] => javaCollection map asString toString
      case seq: Seq[_]                  => seq map asString toString
      case javaMap: java.util.Map[_, _] => asString(mapAsScalaMap(javaMap))
      case map: Map[_, _]               => map mapValues asString toString
      case njo: NativeJavaObject        => asString(njo.unwrap())
      case _: Undefined                 => "()"
      case y => "" + y
    }
  }

//  @Export def transformer(function: Function) = new ClassFileTransformer() {
//
//    def transform(loader: ClassLoader, className: String, classBeingRedefined: Class[_], protectionDomain: ProtectionDomain, classfileBuffer: Array[Byte]) =
//    {
//      function.call(context, scope, null, Array(classfileBuffer)) match {
//        case null                => null
//        case result: Array[Byte] => result
//        case x => throw new UnsupportedOperationException("expected a byte array, got " + x)
//      }
//    }
//  }

  def out: Writer = Context.getCurrentContext.getThreadLocal("out").asInstanceOf[Writer]

  lazy val dumper = new Function1[AnyRef, Unit] { def apply(x: AnyRef) { out.write("" + x); asFunction(this) }}

  @Export def lgr = dumper

  @Export def asf1(f: Object) = f.asInstanceOf[Function1[AnyRef, Any]]

  @Export def asf(f: Object) = asFunction(f.asInstanceOf[Function1[AnyRef, Any]])

  @Export(name="log") def jsLog(x: AnyRef) { dumper.apply(x) }

  @Export def include(path: String) = eval(new FileReader(path), path)

  def eval(in: Reader, filename: String): String = {
    val result: AnyRef = context.evaluateReader(scope, in, filename, 1, null)
    asString(result)
  }

  val devNull: Writer = new Writer {
    def close() {}
    def flush() {}
    def write(cbuf: Array[Char], off: Int, len: Int) {}
  }

  def apply(in: Reader, filename: String, out: Writer): String = try {
    require(out != null, "Wrong call, writer cannot be null")
    Context.enter
    Context.getCurrentContext.putThreadLocal("out", out)
    eval(in, filename)
  } finally {
    Context.exit()
  }

  def apply(in: Reader, filename: String): String = apply(in, filename, devNull)

  def apply(script: String, out: Writer = devNull): String = apply(script, "", out)

  apply(new InputStreamReader(getClass.getResourceAsStream("/kopala.js")), "kopala.js")
  log ("successfully read kopala.js")
}

