(ns frutil.db.tx
  (:require
   [datascript.core :as datascript]

   [frutil.devtools :as dev]

   [frutil.db.query :as q]))


(def with datascript/with)
(def db-with datascript/db-with)

(def q q/q)
(def entity q/entity)


(defn db-with-component
  "returns db with the new entity `component` and references to it specified in
  `parent-eas` as a vector of `[e a]`"
  [db component parent-eas]
  (let [component (assoc component :db/id -1)
        report (with db [component])
        id (get-in report [:tempids -1])
        db (get report :db-after)
        tx-data (into [] (map (fn [[e a]] [:db/add e a id]) parent-eas))]
    (db-with db tx-data)))

