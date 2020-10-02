(ns frutil.ego.db)

;;;

(defn entity-map->tx-data [entity]
  (let [id (get entity :ego.entity/id -1)]
    (map
     (fn [[k v]] [:add id k v])
     (dissoc entity :ego.entity/id))))

(comment
  (entity-map->tx-data {:vorname :witoslaw :nachname :koczewski}))

;;;

(defn new-db []
  {:last-id 0
   :eav {}})
   :ave {}


(defn last-id [db]
  (get db :last-id))


(defn facts [db e a]
  (get-in db [:eav e a]))

(defn es-by-av [db a v]
  (get-in db [:ave a v]))

(defn with-add [db e a v]
  (let [e (if (< e 0)
            (get db :last-id)
            e)]
    (-> db
        (update-in [:eav e a]
                   #(conj (or % #{}) v))
        (update-in [:ave a v]
                   #(conj (or % #{}) e)))))


(defn with-add-component [db parent-id parent-attr]
  (let [id (-> db :last-id inc)]
    (-> db
        (assoc :last-id id)
        (with-add parent-id parent-attr id))))


(def ops
  {:add with-add
   :add-component with-add-component})


(defn with-tx-data-item [db tx-data-item]
  (let [op (first tx-data-item)
        f (get ops op)]
    (when-not f (throw (ex-info (str "Unsupported operation: " op)
                                {:op op
                                 :tx-data-item tx-data-item})))
    (apply f (into [db] (rest tx-data-item)))))


(defn with [db tx-data]
  {:db-before db
   :tx-data tx-data
   :db-after (reduce with-tx-data-item db tx-data)})


(defn db-with [db tx-data]
  (-> db
      (with tx-data)
      (get :db-after)))


;; (defn with [db tx-data]
;;   {:db-before db
;;    :db-after db})
