(ns frutil.ego.schema
  (:require
   [frutil.util :as u]
   [frutil.ego.db :as db]))


(defn entity-id-by-ident [db ident]
  (-> db
      (db/es-by-av :ego.entity/ident ident)
      first))

(defn attr-id-by-ident [db ident]
  (-> db
      (db/es-by-av :ego.attr/ident ident)
      first))

(defn db-with-entity
  [db entity]
  (let [ident (u/sget entity :ego.entity/ident)
        id (entity-id-by-ident db ident)
        tx-data (concat
                 (when-not id [[:add-component 0 :ego.root/entities]])
                 (db/entity-map->tx-data entity))]
    (db/db-with db tx-data)))

(defn db-with-attr
  [db attr]
  (let [ident (u/sget attr :ego.attr/ident)
        entity-ident (keyword (namespace ident))
        entity-id (entity-id-by-ident db entity-ident)
        _ (when-not entity-id
            (throw (ex-info (str "entity schema definition does not exist: " entity-ident)
                            {:attr attr
                             :missing-entity entity-ident})))
        id (attr-id-by-ident db ident)
        tx-data (concat
                 (when-not id [[:add-component entity-id :ego.entity/attrs]])
                 (db/entity-map->tx-data attr))]
    (db/db-with db tx-data)))
