(ns frutil.db.core
  (:require
   [clojure.string :as str]

   [datascript.core :as d]

   [malli.core :as m]
   [malli.error :as me]

   [frutil.util :as u]
   [frutil.devtools :as dev]))




(defn datascript-schema-from-entities
  "Converts schema entities into a DataScript schema."
  [entities]
  (reduce (fn [dss entity]
            (let [ident (get entity :db/ident)
                  dss (if (= :db.type/ref (get entity :db/type))
                        (assoc-in dss [ident :db/type] :db.type/ref)
                        dss)
                  dss (if (= :db.cardinality/many (get entity :db/cardinality))
                        (assoc-in dss [ident :db/cardinality] :db.cardinality/many)
                        dss)]
              dss))
          {} entities))


(defn new-db [{:keys [schema]}]
  (let [
        db (-> (d/empty-db (datascript-schema-from-entities schema))
               (d/db-with schema))]
    {:db db}))



(defn serializable-value [this]
  (select-keys this [:db :modules]))


(defn tempid [this tempid]
  (get-in this [:tx-report :tempids tempid]))




(defn entity [this id]
  (d/entity (get this :db) id))


(defn q [this query]
  (d/q query (get this :db)))


(defn q-es-wheres
  "Finds entity ids.

  Example: `(db/q-es-wheres '[?e :db/ident _])`"
  [this & wheres]
  (let [query (into '[:find ?e :where ] wheres)]
    (map first
         (q this query))))


(defn attribute-is-reverse-ref? [a]
  (-> a name (.startsWith "_")))


(defn attribute-value-type [this a]
  (-> (entity this [:db/ident a])
      :db/valueType))


(defn attribute-is-ref? [this a]
  (if-let [type (attribute-value-type this a)]
    (= type :db.type/ref)
    (attribute-is-reverse-ref? a)))


(defn attribute-cardinality [this a]
  (-> (entity this [:db/ident a])
      :db/cardinality))


(defn attribute-is-many? [this a]
  (if-let [cardinality (attribute-cardinality this a)]
    (= cardinality :db.cardinality/many)
    (attribute-is-reverse-ref? a))) ;; FIXME handle unique attributes


(defn transact [this tx-data]
  (let [db (get this :db)
        report (d/with db tx-data)]
    (assoc this
           :db (get report :db-after)
           :tx-report report)))

(defn facts [this e]
  nil)


;;; common queries


(defn q-ref-attributes-idents [this]
  (map first
      (q this
         '[:find ?a
           :where
           [?e :db/ident ?a]
           [?e :db/valueType :db.type/ref]])))


(defn as-reverse-ref-attr [k]
  (let [ns (namespace k)
        name (name k)]
    (keyword ns (str "_" name))))


(defn q-reverse-ref-attributes-idents [this]
  (map as-reverse-ref-attr (q-ref-attributes-idents this)))
