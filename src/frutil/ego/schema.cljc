(ns frutil.ego.schema
  (:require
   [frutil.util :as u]
   [frutil.ego.db :as db]))


(defn entity-id-by-ident [db ident]
  (-> db
      (db/es-by-av :ego.entity/ident ident)
      first))

(defn with-entity
  [db entity]
  (let [ident (u/sget entity :ego.entity/ident)
        id (entity-id-by-ident db ident)
        tx-data (concat
                 (when-not id [[:add-component 0 :ego.entities]])
                 (db/entity-map->tx-data entity))]
    (db/with db tx-data)))
