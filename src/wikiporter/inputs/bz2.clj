(ns wikiporter.inputs.bz2
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [clojure.data.xml :as xml]
            [clojure.java.io :as io]
            [wikiporter.util :as util])
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

(defn new-reader [filepath]
  (map->BZ2Reader {:filepath filepath}))

(defn parse [reader]
  "Given a streaming reader component, call xml/parse on it."
  (xml/parse (:reader reader)))

(defn transform
  "Pull the :content out of the parsed xml"
  [reader]
  (:content (parse reader)))

(defn match-tag
  "Match a passed in element to a goal tag type"
  [element tag-type]
  (= (:tag element) tag-type))

(defn is-page
  "Return a boolean whether the element is a page, i.e. whether :tag
  == :page"
  [element]
  (match-tag element :page))

(defn only-pages
  "Filter out elements that aren't pages"
  [elements]
  (filter is-page elements))

;; XML parsing logic; borrowed and adapted from the clojure version of
;; wikiparser

(defn elem->map
  "Turns a list of elements into a map keyed by tag name. This doesn't
  work so well if tag names are repeated"
  ;; TODO: tag names should probably be put into a collection (list?)
  [mappers]
  (fn [elems]
    (reduce (fn [m elem]
              (if-let [mapper ((:tag elem) mappers)]
                (assoc m (:tag elem) (mapper elem))
                m))
            {}
            elems)))

(def text-mapper (comp first :content))

(def int-mapper #(Integer/parseInt (text-mapper %)))

;; This returns a fn so we can place it inside a map for later use
(defn get-in-attr
  "Get a value from inside an attributes map"
  [attr]
  (fn [{attrs :attrs}]
    (get attrs attr)))

(def revision-mapper
  "A map of tag types to value converting functions for revision
  elements"
  (comp
   (elem->map
    {:text text-mapper
     :timestamp (comp util/to-timestamp text-mapper)
     :format text-mapper})
   :content))

(def page-mappers
  "A map of tag types to value converting functions for page elements"
  {:title    text-mapper
   :ns       int-mapper
   :id       int-mapper
   :redirect (get-in-attr :title)
   :revision revision-mapper})

(defn xml->maps
  "Take xml elements and turn them into native data"
  [parsed]
  (map (comp (elem->map page-mappers) :content) parsed))

(defn xml->pages
  "Take xml elements, filter out elements that aren't pages, and turn
  them into native data"
  [parsed]
  (xml->maps (only-pages parsed)))
