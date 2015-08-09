(ns wikiporter.core
  (:gen-class)
  (:require [wikiporter.readers.bz2 :as r.bz2]
            [clojure.tools.cli :as cli]
            [clojure.pprint :as pp]
            [com.stuartsierra.component :as component]
            [wikiporter.util :as util]))

(def inputs
  {:bz2 r.bz2/new-reader})

(def input-fns
  {:bz2 r.bz2/parse-xml})

(def filter-fns
  {:content :content
   :identity identity
   :pages })

(defn input-system [input-type uri]
  (component/system-map
   :input ((input-type inputs) uri)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
