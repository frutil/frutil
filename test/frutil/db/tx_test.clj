(ns frutil.db.tx-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :refer [are deftest is testing]]
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

(s/fdef tx/db-with-component
  :args (s/cat :db ::db
               :component ::entity
               :parent-eas (s/coll-of (s/cat :e ::e
                                             :a ::a)
                                      :kind vector?)))

(defexpect db-with-component-test
  (expect "Siddhartha, der Brahmane"
          (-> (test-db)
              (tx/db-with-component
               {:chapter/title "Siddhartha, der Brahmane"}
               [[[:book/title "Siddharta"] :book/chapters]])
              (d/entity [:book/title "Siddharta"])
              :book/chapters
              first
              :chapter/title)))

(comment
  (-> (test-db) :schema :book/chapters))

;;;

(st/instrument)

;;;

(comment
  (def db (test-db)))
