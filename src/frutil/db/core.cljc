(ns frutil.db.core
  (:require
   [clojure.string :as str]

   [datascript.core :as d]

   [malli.core :as m]
   [malli.error :as me]

   [frutil.devtools :as dev]))


(def default-schema
  [{:db/ident  :db/ident
    :db/unique :db.unique/identity}

   {:db/ident       :db.root/ident
    :db/unique      :db.unique/identity
    :db/valueType   :db.type/keyword}

   {:db/ident       :db.root/attrs
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/isComponent  true}

   {:db/ident       :db.root/children
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/isComponent  true
    :db/noHistory    true}])


(defn schema->datascript
  "Converts schema entities into a DataScript schema."
  [schema]
  (reduce (fn [dss entity]
            (let [ident (get entity :db/ident)
                  dss (if (= :db.type/ref (get entity :db/type))
                        (assoc-in dss [ident :db/type] :db.type/ref)
                        dss)
                  dss (if (= :db.cardinality/many (get entity :db/cardinality))
                        (assoc-in dss [ident :db/cardinality] :db.cardinality/many)
                        dss)]
              dss))
          {} schema))

(comment
  (schema->datascript default-schema))


(defn new-db [{:keys [schema modules]}]
  (let [
        schema (reduce (fn [schema module]
                         (concat schema (get module :schema)))
                       schema modules)
        schema (concat schema default-schema)
        db (-> (d/empty-db (schema->datascript schema))
               (d/db-with [{:db.root/ident :singleton}])
               (d/db-with schema))]
    {:db db
     :modules modules}))


(defn tempid [this tempid]
  (get-in this [:tx-report :tempids tempid]))


(defn modules [this]
  (get this :modules))


(defn entity [this id]
  (d/entity (get this :db) id))


(defn q [this query]
  (d/q query (get this :db)))


(defn root-id [this]
  (-> this
      (q '[:find ?e
            :where
           [?e :db.root/ident :singleton]])
      first
      first))


(defn attribute-value-type [this a]
  (-> (entity this [:db/ident a])
      :db/valueType))

(defn attribute-is-ref? [this a]
  (= (attribute-value-type this a) :db.type/ref))


(defn attribute-cardinality [this a]
  (-> (entity this [:db/ident a])
      :db/cardinality))

(defn attribute-is-many? [this a]
  (= (attribute-cardinality this a) :db.cardinality/many))


(defn transact [this tx-data]
  (let [db (get this :db)
        report (d/with db tx-data)]
    (assoc this
           :db (get report :db-after)
           :tx-report report)))

(defn facts [this e]
  nil)