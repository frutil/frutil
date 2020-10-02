(ns frutil.ego.core
  (:require
   [frutil.ego.db :as db]
   [frutil.ego.schema :as schema]))




(defn with-initialized-schema
  [db]
  (-> db
      (schema/with-entity {:ego.entity/ident :ego.root})
      :db-after
      (schema/with-entity {:ego.entity/ident :ego.entity})
      :db-after
      (schema/with-entity {:ego.entity/ident :ego.attr})))


(defn new-db [opts]
  (-> (db/new-db)
      with-initialized-schema
      :db-after))


(comment
  (def db (new-db {})))


;;; tasks

;; create a "Person"

;; add "Anzeigename" to Person


;;; decisions

;; root entity
;;
;; the db always has one root entity
