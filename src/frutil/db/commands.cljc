(ns frutil.db.commands
  (:require
   [frutil.db.core :as db]))


(defn commands [db e]
  (reduce (fn [ret module]
            (if-let [module-commands (get module :commands)]
              (concat ret (module-commands db e))
              ret))
          [] (db/modules db)))


(defn execute [db e command]
  (let [f (or (get command :f) identity)]
    (f db e)))
