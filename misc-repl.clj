;; This file exists primarily as breadcrumbs/usage notes, though
;; converting it to a user.clj or dev namespace probably isn't a bad
;; idea.

;; A basic test system and some utility functions. I should probably
;; look into tying in the 'reloaded' development pattern, now that I
;; seem to be recreating it.
(def porter
  (wikiporter.core/system
   :bz2 "wikisample.short.xml.bz2" :postgres "config.edn"))

(defn stop [] (alter-var-root #'porter component/stop))

(defn start [] (alter-var-root #'porter component/start))

(defn reset [] (stop) (start))

;; This is a quick example for using the postgres component outside of
;; any of the functions in postgres.clj
#_(clojure.java.jdbc/with-db-connection
    [conn {:datasource (get-in porter [:output :datasrc])}]
    (clojure.java.jdbc/query conn "select * from pages"))

;; This is the current method for parsing the xml input with the bz2
;; reader. It's far from perfect, but it's a good jumping off point
;; for experimenting with/testing the system
#_((:bz2 wikiporter.core/input-fns) (:input porter))

;; The current test db I'm using only has one table, so I flatten the
;; map of the returned xml data. This isn't particularly flexible --
;; I'm hard coding the flatten-map function into it -- but it should
;; suffice for now
#_(out.psql/insert-maps
   (:output porter)
   (util/flatten-map
    ((:bz2 wikiporter.core/input-fns) (:input porter))
    "-"))
