(ns frutil.db.tx-test
  (:require
   [clojure.spec.alpha :as s]
   [expectations.clojure.test :refer
    [defexpect expect expecting approximately between between' functionally
     side-effects]]
   [clojure.spec.test.alpha :as st]

   [datascript.core :as d]

   [frutil.db.datascript :as du]
   [frutil.db.query-test :as query-test]
   [frutil.db.tx :as tx]))


(s/def ::db d/db?)
(s/def ::id int?)
(s/def ::a qualified-keyword?)
(s/def ::v (s/or :string string?
                 :keyword keyword?
                 :int int?))
(s/def ::lookup-ref (s/cat :a ::a
                           :v ::v))
(s/def ::e (s/or :id ::id
                 :lookup-ref ::lookup-ref))
(s/def ::entity (s/map-of ::a ::v))

;;;

(defn test-db [] (query-test/test-db))

;;;

(comment
  (def db (test-db))
  (tx/add-component {:chapter/title "Siddhartha, der Samana"}
                    [[[:book/title "Siddhartha"] :book/chapters]])
  (-> db
      (d/db-with '[{:db/id -1
                    :chapter/title "Siddhartha, der Samana"}
                   [:db/add [:book/title "Siddhartha"] :book/chapters -1]])))

;;;

(defn db-with-tx [db tx-f & args]
  (d/db-with db (apply tx-f (into [db] args))))

;;;

(s/fdef tx/add-component
  :args (s/cat :db ::db
               :component ::entity
               :parent-e ::e
               :parent-a ::a))


(defexpect add-component-test
  (expect "Siddhartha, der Brahmane"
          (-> (test-db)
              (db-with-tx
               tx/add-component
               {:chapter/title "Siddhartha, der Brahmane"}
               [:book/title "Siddhartha"] :book/chapters)
              (d/entity [:book/title "Siddhartha"])
              :book/chapters
              first
              :chapter/title)))

(comment
  (-> (test-db) :schema :book/chapters))

;;;

(st/instrument)

;;;

(comment
  (def db (test-db))

  (d/with db [{:db/id -1}])

  (d/with db [{:db/id -1} [:db/add 1 :book/chapters -1]])
                                        ;(tx/add-component db {} [:book/title "Siddhartha"] :book/chapters))

  (-> db
      (d/with [{:db/id -1}])))
