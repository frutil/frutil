(ns frutil.db.datascript
  (:require
   [datascript.core :as d]))


(defn datascript-schema-from-dbident-entities
  "Converts schema entities into a DataScript schema."
  [entities]
  (reduce (fn [dss entity]
            (let [ident (get entity :db/ident)
                  dss (if-let [v (get entity :db/valueType)]
                        (if (= v :db.type/ref)
                          (assoc-in dss [ident :db/valueType] v)
                          dss)
                        dss)
                  dss (if-let [v (get entity :db/cardinality)]
                        (assoc-in dss [ident :db/cardinality] v)
                        dss)
                  dss (if-let [v (get entity :db/unique)]
                        (assoc-in dss [ident :db/unique] v)
                        dss)]
              dss))
          {} entities))


(defn new-db-with-schema
  "Creates a new database with schema transacted as entities."
  [schema]
  (-> (d/empty-db (datascript-schema-from-dbident-entities schema))
      (d/db-with schema)))
