(ns frutil.logging.core)


(defmacro log [& args]
  (apply js/console.log args))
