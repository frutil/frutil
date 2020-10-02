(ns frutil.ego.db-test
  (:require
   [clojure.spec.alpha :as s]
   [expectations.clojure.test :refer
    [defexpect expect expecting approximately between between' functionally
     side-effects]]
   [clojure.spec.test.alpha :as st]

   [frutil.ego.db :as db]))


(defexpect with-add-test

  (expect #{42}
          (-> (db/new-db)
              (db/with-add 0 :root/answer 42)
              (db/facts 0 :root/answer)))

  (expect #{42}
          (-> (db/new-db)
              (db/db-with [[:add 0 :root/answer 42]])
              (db/facts 0 :root/answer)))

  (expecting "-1 as last-id"

    (expect #{42}
            (-> (db/new-db)
                (db/with-add -1 :root/answer 42)
                (db/facts 0 :root/answer)))))


(defexpect with-add-component-test

  (expect #{1}
          (-> (db/new-db)
              (db/with-add-component 0 :root/items)
              (db/facts 0 :root/items)))

  (expect #{1}
          (-> (db/new-db)
              (db/db-with [[:add-component 0 :root/items]])
              (db/facts 0 :root/items)))

  (expecting "-1 as last-id"

    (expect #{42}
            (-> (db/new-db)
                (db/db-with [[:add-component 0 :root/items]
                             [:add -1 :root/answer 42]])
                (db/facts 1 :root/answer)))))
