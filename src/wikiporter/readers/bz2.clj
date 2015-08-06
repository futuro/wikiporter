(ns wikiporter.readers.bz2
  (:gen-class) ;; TODO: What does this do?
  (:require [com.stuartsierra.component :as component]
            [clojure.data.xml :as xml]
            [clojure.java.io :as io])
  (:import (org.apache.commons.compress.compressors.bzip2 BZip2CompressorInputStream)))

(defn bz2-reader
  "Returns a streaming Reader for the given compressed BZip2 file."
  [filename]
  (-> filename io/file io/input-stream BZip2CompressorInputStream. io/reader))

(defrecord BZ2Reader [filepath]
  component/Lifecycle

  (start [component]
    (println ";; Creating streaming BZ2 reader")
    (let [rdr (bz2-reader filepath)]
      (assoc component :reader rdr)))

  (stop [component]
    (println ";; Closing the streaming BZ2 reader")
    (when-let [reader (:reader component)]
      (.close reader))
    (assoc component :reader nil)))
