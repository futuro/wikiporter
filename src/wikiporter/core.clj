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

(def porter-system
  "The system var used from -main"
  nil)

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

(def output-fns
  "Map of output types to functions handling the parsed input data."
  {:postgres out.psql/insert-maps})

#_(def filter-fns
  {:content :content
   :identity identity
   })

(defn system [input-type input-uri output-type config-path]
  (let [config (edn/read-string (slurp config-path))]
   (component/system-map
    :config-options config
    :input ((input-type inputs) input-uri)
    :input-fn (input-type input-fns)
    :output ((output-type outputs) config)
    :output-fn (output-type output-fns))))

(defn start-system
  "Start the passed in system var"
  [sysvar]
  (alter-var-root sysvar component/start))

(defn stop-system
  "Stop the passed in system var"
  [sysvar]
  (alter-var-root sysvar component/stop))

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
   ["-u" "--input-uri URI" "The URI to pass to the input component"
    :default nil
    :id :inputuri]
   ["-o" "--output OutputKey" "Desired output type"
    :default :postgres
    :id :output
    :parse-fn str->keyword]
   ["-b" "--batch-size batch-size" "Number of elements to parse at once"
    :default 512
    :id :batchsize
    :parse-fn #(Integer/parseInt %)]
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

(defn exit
  ([status]
   (System/exit status))
  ([status msg]
   (println msg)
   (System/exit status)))

(defn port
  "Transport data from the input to the output, going through whichever filters
  are chosen for."
  [{:keys [input input-fn output output-fn] :as sysmap} batch-size]
  (dorun (pmap (fn [elems]
                 (->> elems
                      (util/flatten-map "-")
                      (output-fn output :pages)))
               (partition-all batch-size (input-fn input)))))

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
    (let [input-type (:input options)
          input-uri (:inputuri options)
          output-type (:output options)
          config-path (:config-path options)
          batch-size (:batchsize options)]
      (alter-var-root #'porter-system
                      (constantly
                       (system input-type input-uri output-type config-path)))
      (start-system #'porter-system)
      (port porter-system batch-size)
      (stop-system #'porter-system)
      (exit 0))))
