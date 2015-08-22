(ns wikiporter.util
  (:gen-class)
  (:require [clj-time [core :as t] [format :as tf] [coerce :as tc]]
            ))

(defn flattr
  [separator pre themap]
  (map
   (fn [[k v]]
     (let [prefix (if (and (not (empty? pre)) pre)
                    (str pre separator (name k))
                    (name k))]
       (if (map? v)
         (flattr separator prefix v)
         {(keyword prefix) v})))
   themap))

;; Adapted from
;; https://stackoverflow.com/questions/17901933/flattening-a-map-by-join-the-keys
(defn flatten-map
  ([separator nested-map]
   (flatten-map separator nil nested-map))
  ([separator pre nested-map]
   #_(apply merge
          (r/foldcat
           (r/flatten
            (r/mapcat
             (partial flattr separator pre)
             nested-map))))
   (map
    #(apply merge (flatten (flattr separator pre %)))
    nested-map)))

(defn to-timestamp
  [timestr]
  (let [formatter (tf/formatters :date-time-no-ms)]
    (tc/to-timestamp (tf/parse formatter timestr))))
