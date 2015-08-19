(ns wikiporter.core
  (:gen-class)
  (:require [clojure.edn :as edn]
            [clojure.tools.cli :as cli]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [wikiporter.util :as util]
            [wikiporter.inputs.bz2 :as in.bz2]
            [wikiporter.outputs.postgres :as out.psql]))

(def inputs
  "Map of input types to component construction functions. Currently,
  every input component must take one argument, typically a uri
  specifying where to find its source."
  {:bz2 in.bz2/new-reader})

(def input-fns
  "Map of input types to functions returning parsed input."
  {:bz2 (comp in.bz2/xml->pages in.bz2/transform)})

(def outputs
  "Map of output types to component construction functions. Currently,
  every input component must take one argument, a config map built
  from the config-path."
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

(defn str->keyword
  "Turn a string into a keyword, stripping the first leading colon (:)
  if it exists."
  [string]
  (keyword (str/replace-first string ":" "")))

(def cli-options
  [["-c" "--config config-path" "Path to the config file"
    :default "config.edn"
    :id :config-path]
   ["-i" "--input InputKey" "Desired input type"
    :default :bz2
    :id :input
    :parse-fn str->keyword]
   ["-o" "--output OutputKey" "Desired output type"
    :default :postgres
    :id :output
    :parse-fn str->keyword]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Usage: wikiporter [options]"
        ""
        "Options:"
        options-summary
        ""]
       (str/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (str/join \newline errors)))


(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main
  "Main entrance point from the command line"
  ;; Note to self
  ;; args is a list of the space delimited things passed on the cli
  [& args]
  (let [{:keys [options arguments errors summary] :as opts} (cli/parse-opts args cli-options)]
    ;; Handle help and error conditions
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))
    ;; Execute program with options
    #_(case (first arguments)
      "start" (server/start! options)
      "stop" (server/stop! options)
      "status" (server/status! options)
      (exit 1 (usage summary)))))
