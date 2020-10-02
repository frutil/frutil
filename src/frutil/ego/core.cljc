(ns frutil.ego.core
  (:require
   [frutil.ego.db :as db]
   [frutil.ego.schema :as schema]))




(defn db-with-initialized-schema
  [db]
  (-> db
      ;; root
      (schema/db-with-entity {:ego.entity/ident :ego.root})
      (schema/db-with-attr {:ego.attr/ident :ego.root/entities
                            :ego.attr/type  :component
                            :ego.attr/many? true})

      ;; entity
      (schema/db-with-entity {:ego.entity/ident :ego.entity})
      (schema/db-with-attr {:ego.attr/ident :ego.entity/attrs
                            :ego.attr/type  :component
                            :ego.attr/many? true})

      ;; attr
      (schema/db-with-entity {:ego.entity/ident :ego.attr})
      (schema/db-with-attr {:ego.attr/ident :ego.entity/ident
                            :ego.attr/type  :keyword})
      (schema/db-with-attr {:ego.attr/ident :ego.attr/ident
                            :ego.attr/type  :keyword})
      (schema/db-with-attr {:ego.attr/ident   :ego.attr/type
                            :ego.attr/type    :keyword
                            :ego.attr/options #{:component
                                                :keyword
                                                :string
                                                :boolean
                                                :set}})
      (schema/db-with-attr {:ego.attr/ident :ego.attr/many?
                            :ego.attr/type  :boolean})
      (schema/db-with-attr {:ego.attr/ident :ego.attr/options
                            :ego.attr/type  :set})))


(defn new-db [opts]
  (-> (db/new-db)
      db-with-initialized-schema))


(comment
  (def db (new-db {})))


;;; tasks

;; create a "Person"

;; add "Anzeigename" to Person


;;; decisions

;; root entity
;;
;; the db always has one root entity
