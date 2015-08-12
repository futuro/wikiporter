(ns wikiporter.core
  (:gen-class)
  (:require [clojure.edn :as edn]
            [clojure.tools.cli :as cli]
            [clojure.pprint :as pp]
            [com.stuartsierra.component :as component]
            [wikiporter.util :as util]
            [wikiporter.inputs.bz2 :as in.bz2]
            [wikiporter.outputs.postgres :as out.psql]))

(def inputs
  {:bz2 in.bz2/new-reader})

(def input-fns
  {:bz2 (comp in.bz2/xml->pages in.bz2/transform)})

(def outputs
  {:postgres out.psql/new-pool})

#_(def filter-fns
  {:content :content
   :identity identity
   })

(defn system [input-type input-uri output-type config-path]
  (let [config (edn/read-string (slurp config-path))]
   (component/system-map
    :config-options config
    :input ((input-type inputs) input-uri)
    :output ((output-type outputs) config))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
