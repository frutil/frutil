(ns frutil.devtools
  #?(:cljs (:require-macros [frutil.devtools]))
  (:require
   #?(:clj [clojure.string :as str])))


(def dev? true) ;; FIXME
(def prod? (not dev?))


(def SPY_IMPL (atom (fn [subject form identifier]
                      (println "SPY:" identifier form subject))))


(defmacro dev-only [expr]
  (when dev?
    ~expr))


#?(:clj
   (defmacro spy [subject]
     (when dev?
       (let [{:keys [file line]} (meta &form)
             file (or file *file*)
             source (str/join ":" (filter some? [file line]))]
         `(let [ret# ~subject]
            (@SPY_IMPL ret# '~subject ~source)
            ret#)))))


(comment
  (macroexpand '(spy (println "hello"))))

;; (defn spy> [subject identifier]
;;   (when-let [spy-impl @SPY_IMPL]
;;     (spy-impl subject identifier)))


;; (defn spy>> [identifier subject]
;;   (spy> subject identifier))
