(ns frutil.db.query-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :refer [are deftest is testing]]
   [clojure.spec.test.alpha :as st]

   [datascript.core :as d]

   [frutil.db.datascript :as du]
   [frutil.db.query :as query]))


(s/def ::db d/db?)


;;;


(defn test-db []
  (-> [;; author
       {:db/ident       :author/name
        :db/unique      :db.unique/identity}
       {:db/ident       :author/followers
        :db/type        :db.type/ref
        :db/cardinality :db.cardinality/many}

       ;; book
       {:db/ident       :book/title
        :db/unique      :db.unique/identity}
       {:db/ident       :book/author
        :db/valueType   :db.type/ref}
       {:db/ident       :book/chapters
        :db/valueType   :db.type/ref
        :db/cardinality :db.cardinality/many
        :db/isComponent true}

       ;; chapter
       {:db/ident        :chapter/title
        :db/valueType    :db.type/string}]
      du/new-db-with-schema
      (d/db-with [{:author/name "Hermann Hesse"}
                  {:book/title  "Siddharta"
                   :book/author [:author/name "Hermann Hesse"]}])))

;;;

(s/fdef query/es-wheres
  :args (s/cat :db ::db :wheres (s/+ vector?)))

(comment
  (query/es-wheres db '[?e :db/ident _]))

;;;

(s/fdef query/attribute-is-reverse-ref?
  :args (s/cat :a keyword?)
  :ret boolean?)

(deftest attribute-is-reverse-ref?-test
  (are [a] (= true a)
    (query/attribute-is-reverse-ref? :hello/_world)
    (query/attribute-is-reverse-ref? :_hello))
  (are [a] (= false a)
    (query/attribute-is-reverse-ref? :hello/world)
    (query/attribute-is-reverse-ref? :hello)))

;;;

(s/fdef query/attribute-is-ref?
  :args (s/cat :db ::db :a keyword?)
  :ret boolean?)

(deftest attribute-is-ref?-test
  (let [db (test-db)]
    (is (= true (query/attribute-is-ref? db :book/author)))
    (is (= true (query/attribute-is-ref? db :book/_author)))
    (is (= false (query/attribute-is-ref? db :book/title)))))

;;;

(s/fdef query/attribute-is-many?
  :args (s/cat :db ::db :a keyword?)
  :ret boolean?)

(deftest attribute-is-manyf?-test
  (let [db (test-db)]
    (is (= true (query/attribute-is-many? db :author/followers)))
    (is (= true (query/attribute-is-many? db :book/_author)))
    (is (= false (query/attribute-is-many? db :book/title)))))

;;;

(s/fdef query/ref-attributes-idents
  :args (s/cat :db ::db))

(deftest ref-attributes-idents-test
  (let [db (test-db)]
    (is (= #{:book/author :book/chapters} (into #{} (query/ref-attributes-idents db))))))

;;;

(s/fdef query/reverse-ref-attributes-idents
  :args (s/cat :db ::db))

(deftest reverse-ref-attributes-idents-test
  (let [db (test-db)]
    (is (= #{:book/_author :book/_chapters} (into #{} (query/reverse-ref-attributes-idents db))))))

;;;

(st/instrument)

;;;

(comment
  (def db (test-db)))
