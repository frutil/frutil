(ns frutil.util
  (:refer-clojure :exclude [read-string random-uuid])
  (:require
   [clojure.pprint :refer [pprint]]
   [#?(:cljs cljs.reader :clj clojure.edn) :refer [read-string]]))


(defn decode-edn
  [s]
  (when s (read-string s)))


(defn encode-edn
  ([value]
   (pr-str value))
  ([value pretty?]
   (if pretty?
     (with-out-str (pprint value))
     (pr-str value))))
