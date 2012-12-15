var DEBUG = false

function debug(s) { if (DEBUG) log(s) }

function iterable2array(iterable) {
  var out = []
  var i = iterable.iterator()
  while (i.hasNext()) out.push(asJS(i.next()))
  return out;
}

function pair2js(tuple, out) {
  if (tuple.getKey && tuple.getValue) out[tuple.getKey()] = asJS(tuple.getValue())
  else if (tuple._1 && tuple._2)      out[tuple._1]       = asJS(tuple._2())
}

function map2js(map) {
  var out = {};
  if (map.iterator) {
    var i = map.iterator();
    while(i.hasNext()) {
      pair2js(i.next(), out)
    }
  } else if(map.entrySet) {
    return map2js(map.entrySet())
  } else if (map.getKey && map.getValue) {
    pair2js(map, out)
  } else return map
  return out;
}

function isNull(x) { return x == null }

// need this function, otherwise Rhino throws warnings about comparing non-js objects
function notNull(x) { return !isNull(x)}

function isJava(x) { return notNull(x) && !!x.getClass }

var lists = {"Nil":0, "Array":1, "List":2, "$colon$colon":3}

function isaList(clazz) {
  var name = clazz.getSimpleName()
  for (type in lists) {
    if (name.indexOf(type) >= 0) {
      return true;
    }
  }
  return false;
}

function isaMap(clazz) {
  return clazz.getSimpleName().indexOf("Map") >= 0
}

function isArray(clazz) { return clazz.getSimpleName().indexOf("[]") > 0 }

function array2js(array) {
  var out = [];
  for (var i = 0; i < array.length; i++) {
    out[i] = asJS(array[i])
  }
  return out;
}

function asJS(obj) {
  try {
    if (isNull(obj)) return obj
    if (isJava(obj)) {
      var clazz = obj.getClass()
      var cn = clazz.getSimpleName ? clazz.getSimpleName() : clazz.getName()
      if (isaMap(clazz))  return map2js(obj)
      if (isaList(clazz)) return iterable2array(obj)
      if (isArray(clazz)) return array2js(obj)
      if (obj.iterator) return iterable2array(obj)
    }
  } catch (x) {
    log("oops... " + x + ": " + obj + "\n")
    throw x
  }
  return obj;
}

function constructors(clazz) {ntl
  return asJS(clazz.getDeclaredConstructors())
}

function aNew(name) {
  var clazz = java.lang.Class.forName(name)
  var cons = constructors(clazz)
  return clazz.newInstance()
}

function aNew1(name, arg) {
  var clazz = java.lang.Class.forName(name)
  var cons = constructors(clazz)
  var args = []
  for (var i = 0; i < cons.length; i++) {
    var c = cons[i]
    var params = c.getParameterTypes()
    if (params.length == 1) {
      bkpt(1111)
      return c.newInstance(arg)
    }
  }
  fail("Could not find a single-param constructor for " + name)
}

function $field(clazz, name) {
  for (var c = clazz; notNull(c); c = c.getSuperclass()) {
    try {
      var f = c.getDeclaredField(name);
      f.setAccessible(true);
      return f;
    } catch (x) {}
  }
  throw new Error("Could not find field " + name + " of " + clazz)
}

function $F(object, name) {
  return $field(object.getClass(), name).get(object);
}

function $S(object, name, value) {
  $field(object.getClass(), name).set(object, value);
}

function makePublic(object, method) {
  for (var c = object.getClass(); notNull(c); c = c.getSuperclass()) {
    try {
      c.getDeclaredMethod(method).setAccessible(true)
      return
    } catch (x) {}
  }
  throw new Error("Could not find method " + method + " of " + object)
}

function call0(object, method) {
  for (var c = object.getClass(); notNull(c); c = c.getSuperclass()) {
    try {
      var m = c.getDeclaredMethod(method)
      m.setAccessible(true)
      return m.invoke(object)
    } catch (x) {}
  }
  throw new Error("Could not find method " + method + " of " + object)
}

function list() {
  var l = aNew("java.util.LinkedList")
  for (i = 0; i < arguments.length; i++) { l.push(arguments[i]) }
  return l
}

function include(script) {
  if (sourceDir) {
    source(sourceDir + "/" + script)
  } else {
    throw new Error("sourceDir must be specified")
  }
}

function sleep(ms) { java.lang.Thread.currentThread().sleep(ms) }

function optionalText(text) {
  return text ? (" - " + text) : ""
}

function hush() { return "" }

function spaces(n) {
  for (i = 0; i < n; i++) log(' ')
}

function fail(message) {throw new Error(message)}

function assert(condition, message) { if (!condition) fail(message) }

hush() // make sure a function is not returned from the script

