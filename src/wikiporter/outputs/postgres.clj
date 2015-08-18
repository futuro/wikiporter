(ns wikiporter.outputs.postgres
  (:require [clojure.java.jdbc :as sql]
            [com.stuartsierra.component :as component]
            [hikari-cp.core :as hcp]
            [honeysql.core :as honey]
            [honeysql.helpers :as helpers]))

(def ds-config
  {:adapter "postgresql"
   :pool-name "postgres-pool"
   :port-number 5432
   :server-name "localhost"})

(defrecord PostgresDB [config]
  component/Lifecycle
  (start [component]
    (println ";; Creating database connection pool")
    (let [datasrc (hcp/make-datasource
                   (merge ds-config (:hikaricp config)))]
      (assoc component :datasrc datasrc)))
  (stop [component]
    (println ";; Closing database connection pool")
    (if-let [datasrc (:datasrc component)]
        (.close datasrc))
    (assoc component :datasrc nil)))

(defn new-pool
  "Create a new PostgresDB component"
  [config]
  (map->PostgresDB {:config config}))

(defn add-nil-redirects
  "Add nil redirects to page maps which are missing a :redirect kv
  pair."
  [pages]
  (map #(merge {:redirect nil} %) pages))

(defn insert-maps
  "Take in a collection of maps where the keys match column names in
  the table passed in, inserting the values in their respective
  columns."
  [db table nestedmaps]
  (let [maps (add-nil-redirects nestedmaps)
        conn {:datasource (:datasrc db)}]
    (try
      (sql/execute! conn
                    (-> (helpers/insert-into table)
                        (helpers/values maps)
                        honey/format))
      (catch Exception e
        (prn (.getNextException e)))))
  #_(callback nestedmaps))

(defn empty-table!
  "Delete everything in the table"
  [db table]
  (let [conn {:datasource (:datasrc db)}]
    (try
      (sql/delete! conn table nil)
      (catch Exception e
        (prn (.getNextException e))))))
