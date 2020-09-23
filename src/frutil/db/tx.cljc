(ns frutil.db.tx
  (:require
   [datascript.core :as datascript]

   [frutil.devtools :as dev]

   [frutil.db.query :as q]))


(def with datascript/with)
(def db-with datascript/db-with)

(def q q/q)
(def entity q/entity)


(defn add-component
  "returns tx-data for creating a new entity `component` and references to it
  specified in `parent-eas` as a vector of `[e a]`"
  [component parent-eas]
  (into
   [(assoc component :db/id -1)]
   (map (fn [[e a]] [:db/add e a -1]) parent-eas)))


(defn update-fact
  "returns tx-data for updating an existing fact"
  [e a old-v new-v]
  [[:db/retract e a old-v]
   [:db/add e a new-v]])


(defn retract-fact
  "returns tx-data for retracting an existing fact"
  [e a v]
  [[:db/retract e a v]])
