(ns frutil.logging.core
  (:require
   [com.brunobonacci.mulog :as µ]))


(defmacro log [& args]
  (cons `µ/log args))

(comment
  (log :test))


(defn colorize-event [event]
  event)


(defonce initialized
  (do
    (µ/start-publisher! {:type :console
                         :pretty? true
                         :transform #(map colorize-event %)})
    true))
