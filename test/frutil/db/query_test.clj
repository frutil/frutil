(ns frutil.db.query-test
  (:require
   [clojure.spec.alpha :as s]
   [expectations.clojure.test :refer
    [defexpect expect expecting approximately between between' functionally
     side-effects]]
   [clojure.spec.test.alpha :as st]

   [datascript.core :as d]

   [frutil.db.datascript :as du]
   [frutil.db.query :as query]))


(s/def ::db d/db?)


;;;


(defn test-db []
  (-> [;; author
       {:db/ident       :author/name
        :db/valueType   :db.type/string
        :db/unique      :db.unique/identity}
       {:db/ident       :author/followers
        :db/type        :db.type/ref
        :db/cardinality :db.cardinality/many}

       ;; book
       {:db/ident       :book/title
        :db/unique      :db.unique/identity
        :db/valueType   :db.type/string}
       {:db/ident       :book/author
        :db/valueType   :db.type/ref}
       {:db/ident       :book/chapters
        :db/valueType   :db.type/ref
        :db/cardinality :db.cardinality/many
        :db/isComponent true}

       ;; chapter
       {:db/ident        :chapter/title
        :db/valueType    :db.type/string}
       {:db/ident        :chapter/subchapters
        :db/valueType    :db.type/ref
        :db/cardinality  :db.cardinality/many
        :db/isComponent  true}]
      du/new-db-with-schema
      (d/db-with [{:author/name "Hermann Hesse"}
                  {:book/title  "Siddhartha"
                   :book/author [:author/name "Hermann Hesse"]}])))


(defn test-db-with-chapters []
  (-> (test-db)
      (d/db-with [{:db/id [:book/title "Siddhartha"]
                   :book/chapters
                   [{:chapter/title "Samana"}
                    {:chapter/title "Brahmane"
                     :chapter/subchapters [{:chapter/title "Fussnoten"}]}]}])))

;;;

(s/fdef query/es-wheres
  :args (s/cat :db ::db :wheres (s/+ vector?)))

(comment
  (query/es-wheres db '[?e :db/ident _]))

;;;

(s/fdef query/attribute-is-reverse-ref?
  :args (s/cat :a keyword?)
  :ret boolean?)

(defexpect attribute-is-reverse-ref?-test
  (expect (query/attribute-is-reverse-ref? :hello/_world))
  (expect (query/attribute-is-reverse-ref? :_hello))
  (expect (not (query/attribute-is-reverse-ref? :hello/world)))
  (expect (not (query/attribute-is-reverse-ref? :hello))))

;;;

(s/fdef query/attribute-is-ref?
  :args (s/cat :db ::db :a keyword?)
  :ret boolean?)

(defexpect attribute-is-ref?-test
  (let [db (test-db)]
    (expect (query/attribute-is-ref? db :book/author))
    (expect (query/attribute-is-ref? db :book/_author))
    (expect (not (query/attribute-is-ref? db :book/title)))))

;;;

(s/fdef query/attribute-is-many?
  :args (s/cat :db ::db :a keyword?)
  :ret boolean?)

(defexpect attribute-is-manyf?-test
  (let [db (test-db)]
    (expect (query/attribute-is-many? db :author/followers))
    (expect (not (query/attribute-is-many? db :book/_author)))
    (expect (not (query/attribute-is-many? db :book/title)))))

;;;

(s/fdef query/attribute-is-component?
  :args (s/cat :db ::db :a keyword?)
  :ret boolean?)

(defexpect attribute-is-componentf?-test
  (let [db (test-db)]
    (expect (query/attribute-is-component? db :book/chapters))
    (expect (not (query/attribute-is-component? db :book/author)))))

;;;

(s/fdef query/ref-attributes-idents
  :args (s/cat :db ::db))

(defexpect ref-attributes-idents-test
  (let [db (test-db)]
    (expect #{:book/author :book/chapters :chapter/subchapters}
            (into #{} (query/ref-attributes-idents db)))))

;;;

(s/fdef query/reverse-ref-attributes-idents
  :args (s/cat :db ::db))

(defexpect reverse-ref-attributes-idents-test
  (let [db (test-db)]
    (expect #{:book/_author :book/_chapters :chapter/_subchapters}
            (into #{} (query/reverse-ref-attributes-idents db)))))

;;;

(defexpect attributes-of-entity-test
  (let [db (test-db)
        entity (d/entity db [:book/title "Siddhartha"])]
    (expect #{:book/title :book/author}
            (-> entity
                (query/attributes-of-entity (constantly true))
                (->> (into #{}))))))

;;;

(defexpect components-of-test
  (let [db (test-db)]
    (expect empty? (query/components-of db [:book/title "Siddhartha"])))
  (let [db (test-db-with-chapters)]
    (expect #{"Samana"
              "Brahmane"
              "Fussnoten"}
            (->> (query/components-of db [:book/title "Siddhartha"])
                 (map #(d/entity db %))
                 (map :chapter/title)
                 (into #{})))))

;;;

(st/instrument)

;;;

(comment
  (def db (test-db))
  (-> db :schema :book/chapters)
  (def db (test-db-with-chapters))
  (-> (test-db-with-chapters)
      (d/db-with [[:db/retractEntity [:book/title "Siddhartha"]]])
      (->> (d/q '[:find ?e :where [?e :chapter/title _]])))

  (def db (d/db-with
           (d/empty-db {:profile {:db/valueType   :db.type/ref
                                  :db/isComponent true}})
           [{:db/id 1 :name "Ivan" :profile 3}
            {:db/id 3 :email "@3"}
            {:db/id 4 :email "@4"}]))

  (let [db (d/db-with db [[:db/retractEntity 1]])]))
