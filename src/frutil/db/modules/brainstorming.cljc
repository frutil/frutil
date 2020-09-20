(ns frutil.db.modules.brainstorming)


(defn init [db]
  db)


(defn module []
  {:ident :brainstorming
   :init-f init
   :schema [{:db/ident     :brainstorming.idea/label
             :db/valueType :db.type/string}]})
