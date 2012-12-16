package kopala.js

import scala.collection.JavaConversions._
import scalaz._
import Scalaz._
import org.mozilla.javascript._
import java.io._
import collection.immutable.Map
import kopala.js.Adapter._
import kopala.Obvious._
import java.io.Writer
import java.io.Reader

trait Kopala extends ImporterTopLevel {
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

  def names: Array[String] = {
    getClass.getDeclaredMethods filter {
      _.getDeclaredAnnotations exists {_.isInstanceOf[Export]}
    } map { _.getName } toArray
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
//      case njo: NativeJavaObject        => njo.getClassName + "{" + "???" + "}"
      case a: Array[_]                  => a map asString toString
      case javaCollection: java.util.Collection[_]  => asString(collectionAsScalaIterable(javaCollection))
      case seq: Seq[_]                  => seq map asString toString
      case javaMap: java.util.Map[_, _] => asString(mapAsScalaMap(javaMap))
      case map: Map[_, _]               => map mapValues asString toString
      case x: NativeJavaObject          => x.toString
      case _: Undefined                 => ""
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

  lazy val logger = new Function1[AnyRef, Unit] { def apply(x: AnyRef) { out.write("" + x); asFunction(this) }}

  @Export def lgr = logger

  @Export def asf1(f: Object) = f.asInstanceOf[Function1[AnyRef, Any]]

  @Export def asf(f: Object) = asFunction(f.asInstanceOf[Function1[AnyRef, Any]])

  @Export def log(x: AnyRef) { logger.apply(x) }

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
    Context.enter
    Context.getCurrentContext.putThreadLocal("out", out)
    eval(in, filename)
  } finally {
    Context.exit()
  }

  def apply(in: Reader, filename: String): String = apply(in, filename, devNull)

  def apply(script: String, out: Writer = devNull): String = apply(script, "", out)

  apply(new InputStreamReader(getClass.getResourceAsStream("/kopala.js")), "kopala.js")
}

