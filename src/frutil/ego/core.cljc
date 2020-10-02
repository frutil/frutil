(ns frutil.ego.core
  (:require
   [frutil.ego.db :as db]))


(defn new-db [opts]
  (db/new-db))


;;; commands

(defn with-entity [db uid parent-id parent-attr])

;;; tasks

;; create a "Person"

;; add "Anzeigename" to Person


;;; decisions

;; root entity
;;
;; the db always has one root entity
