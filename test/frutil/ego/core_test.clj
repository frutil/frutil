(ns frutil.ego.core-test
  (:require
   [clojure.spec.alpha :as s]
   [expectations.clojure.test :refer
    [defexpect expect expecting approximately between between' functionally
     side-effects]]
   [clojure.spec.test.alpha :as st]

   [frutil.ego.core :as ego]))


;; (defexpect init!-test
;;   (let [conn (ego/create-conn)
;;         report (ego/init! conn)]
;;     (expect :singleton
;;             (ego/fact @conn ego/root-ref :ego.root/ident))))
