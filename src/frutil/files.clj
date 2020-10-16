(ns frutil.files
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]

   [frutil.util :as u]))



(defn write-edn
  ([file data]
   (write-edn file data true))
  ([file data pretty?]
   (let [file (io/as-file file)
         dir (-> file .getParentFile)]
     (when-not (-> dir .exists) (-> dir .mkdirs))
     (spit file (u/encode-edn data pretty?)))))

(defn read-edn
  [file]
  (let [file (io/as-file file)]
    (when (-> file .exists)
      (-> file slurp edn/read-string))))
