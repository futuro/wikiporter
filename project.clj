(defproject wikiporter "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.stuartsierra/component "0.2.3"]
                 [honeysql "0.6.1"]
                 [org.apache.commons/commons-compress "1.9"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/core.incubator "0.1.3"]
                 [org.clojure/data.xml "0.0.8"]
                 [org.clojure/java.jdbc "0.4.1"]
                 [org.clojure/tools.cli "0.3.2"]
                 [clj-time "0.10.0"]]
  :main ^:skip-aot wikiporter.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
