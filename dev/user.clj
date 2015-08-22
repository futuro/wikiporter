(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [wikiporter.core :as core]))

(def short-sample "wikisample.short.xml.bz2")

(reloaded.repl/set-init!
 #(core/system :bz2 short-sample :postgres "config.edn"))
