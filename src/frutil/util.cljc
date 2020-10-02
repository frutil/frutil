(ns frutil.util
  (:refer-clojure :exclude [read-string random-uuid])
  (:require
   [clojure.pprint :refer [pprint]]
   [#?(:cljs cljs.reader :clj clojure.edn) :refer [read-string]]))


(defn sget
  "Safe `get`. Throws exception if value is missing."
  [m k]
  (if-let [v (get m k)]
    v
    (throw (ex-info (str "Map is missing `" k "`")
                    {:m m :k k}))))


(defn decode-edn
  [s]
  (when s
    (try
      (read-string s)
      (catch #?(:cljs :default :clj Exception) ex
        (throw (ex-info "frutil.util/decode-edn failed"
                        {:edn s}
                        ex))))))


(defn encode-edn
  ([value]
   (pr-str value))
  ([value pretty?]
   (if pretty?
     (with-out-str (pprint value))
     (pr-str value))))
