(ns frutil.db.browser
  (:require
   [clojure.string :as str]

   [malli.core :as m]
   [malli.error :as me]

   [frutil.devtools :as dev]
   [frutil.db.core :as db]))




(defn new-browser [db options]
  (let [root-id (db/root-id db)]
    {:db db
     :root root-id
     :cursor root-id}))


(defn db [browser]
  (get browser :db))


(defn q [this query]
  (db/q (db this) query))


(defn entity [this id]
  (db/entity (get this :db) id))


(defn root [browser]
  (get browser :root))


(defn cursor [browser]
  (get browser :cursor))


(defn tempid [this tempid]
  (db/tempid (db this) tempid))


(defn transact [this tx-data]
  (update this :db db/transact tx-data))


(defn attribute-is-ref? [this a]
  (db/attribute-is-ref? (db this) a))


(defn attribute-is-many? [this a]
  (db/attribute-is-many? (db this) a))


(defn add-fact [this a v]
  (let [cursor (cursor this)]
    (transact this [[:db/add cursor a v]])))


(defn add-ref-entity [this a]
  (let [cursor (get this :cursor)
        this (transact this [{:db/id -1
                              :db/dummy :dummy}
                             [:db/add cursor a -1]])
        cursor (tempid this -1)]
    (assoc this :cursor cursor)))
