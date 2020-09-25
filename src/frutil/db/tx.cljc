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
  [_db component parent-eas]
  (into
   [(assoc component :db/id -1)]
   (map (fn [[e a]] [:db/add e a -1]) parent-eas)))


(defn add-fact
  "returns tx-data for adding a new fact"
  [_db e a v]
  [[:db/add e a v]])


(defn update-fact
  "returns tx-data for updating an existing fact"
  [_db e a old-v new-v]
  [[:db/retract e a old-v]
   [:db/add e a new-v]])


(defn retract-fact
  "returns tx-data for retracting an existing fact"
  [db e a v]
  (cond-> [[:db/retract e a v]]
          (q/attribute-is-component? db a)
          (conj [:db/retractEntity v])))
