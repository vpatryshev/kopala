package kopala.js

import org.mozilla.javascript.{Context, Scriptable, Function}

object Adapter {
  def no(msg: String) = sys.error("no " + msg + "()")

  abstract class JsObj extends Scriptable {
    def setPrototype(prototype: Scriptable) {no("setPrototype")}
    def get(index: Int, start: Scriptable) = no("get")
    def get(name: String, start: Scriptable) = no("get")
    def put(index: Int, start: Scriptable, value: Any) {no("put")}
    def put(name: String, start: Scriptable, value: Any) {no("put")}
    def getClassName = no("getClassName")
    def hasInstance(instance: Scriptable) = false
    def setParentScope(parent: Scriptable) {}
    def delete(index: Int) {no("delete")}
    def delete(name: String) {no("delete")}
    def has(index: Int, start: Scriptable) = false
    def getPrototype = no("getPrototype")
    def getIds = no("getIds")
    def getDefaultValue(hint: Class[_]) = no("getDefaultValue")
    def getParentScope = no("getParentScope")
    def has(name: String, start: Scriptable) = false
  }

  def asFunction[X](f: X => Any): Function = new JsObj with Function {

    def call(cx: Context, scope: Scriptable, thisObj: Scriptable, args: Array[AnyRef]) = {
      require(args.length == 1)
      (args(0) match {
        case x: X => f(x)
        case _ => sys.error("Wrong type of " + args(0))
      }) match {
        case y: Object => y
        case bad => sys.error("Expected an Object, got " + bad)
      }
    }

    def construct(cx: Context, scope: Scriptable, args: Array[AnyRef]) = no("construct")
  }
}
