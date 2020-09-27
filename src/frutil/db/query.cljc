(ns frutil.db.query
  (:require
   [datascript.core :as datascript]

   [frutil.devtools :as dev]))


(def q datascript/q)
(def entity datascript/entity)


(defn es-wheres
  "Finds entity ids by given `wheres`. Binds `?e` to the returned ids.

  Example: `(db/q-es-wheres '[?e :db/ident _])`"
  [db & wheres]
  (let [query (into '[:find ?e :where ] wheres)]
    (map first
         (q query db))))


(defn e-wheres
  [db & wheres]
  (first (apply es-wheres (into [db] wheres))))


;;; schema


(defn attribute-is-reverse-ref?
  "returns true if `a` is a reverse reference attribute"
  [a]
  (-> a name (.startsWith "_")))


(defn attribute-value-type
  [db a]
  (-> (entity db [:db/ident a])
      :db/valueType))


(defn attribute-is-ref?
  "returns true if `a` is a reference attribute"
  [db a]
  (if-let [type (attribute-value-type db a)]
    (= type :db.type/ref)
    (attribute-is-reverse-ref? a)))


(defn attribute-cardinality [db a]
  (-> (entity db [:db/ident a])
      :db/cardinality))


(defn attribute-is-many?
  "returns `true` if `a` is a `:db.cardinality/many` attribute"
  [db a]
  (let [cardinality (attribute-cardinality db a)]
    (= cardinality :db.cardinality/many)))


(defn attribute-is-component?
  "returns `true` if `a` is a `:db/isComponent` attribute"
  [db a]
  (-> (entity db [:db/ident a])
      :db/isComponent
      boolean))


(defn attributes-of-entity
  [entity attribute-entity-predicate]
  (let [db (datascript/entity-db entity)]
    (->> entity
         keys
         (map #(datascript/entity db [:db/ident %]))
         (filter attribute-entity-predicate)
         (map :db/ident))))


(defn attributes-in-schema
  "returns all attributes defined in the schema"
  [db filter-f]
  (->> (q '[:find ?e :where [?e :db/ident _]]
          db)
       (map first)
       (map #(entity db %))
       (filter filter-f)
       (map :db/ident)))


(defn ref-attributes-idents
  "returns the `:db/ident` values of all reference attributes"
  [db]
  (map first
       (q '[:find ?a
            :where
            [?e :db/ident ?a]
            [?e :db/valueType :db.type/ref]]
          db)))


(defn as-reverse-ref-attr
  "returns the reverse reference attribute for a given attribute"
  [k]
  (let [ns (namespace k)
        name (name k)]
    (keyword ns (str "_" name))))


(defn reverse-ref-attributes-idents
  "returns all reverse reference attributes"
  [db]
  (map as-reverse-ref-attr (ref-attributes-idents db)))


;;; components

(defn components-of
  "returns a set of ids which are the components or components of components
  of `e`."
  [db e]
  (let [entity (datascript/entity db e)]
    (reduce (fn [ids attr]
              (reduce (fn [ids component]
                        (let [component-id (get component :db/id)]
                          (if (ids component-id)
                            ids
                            (-> ids
                                (conj component-id)
                                (into (components-of db component-id))))))
                      ids (get entity attr)))
            #{} (attributes-of-entity entity :db/isComponent))))
