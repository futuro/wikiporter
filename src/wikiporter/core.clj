(ns wikiporter.core
  (:gen-class)
  (:require [wikiporter.readers.bz2 :as r.bz2]
            [com.stuartsierra.component :as component]))

(defn reader-system [filepath]
  (component/system-map
   :reader (r.bz2/new-reader filepath)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
