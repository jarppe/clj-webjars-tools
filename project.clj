(defproject clj-webjars-tools "0.1.0-SNAPSHOT"
  :description "Clojure tool to locate WebJars from classpath"
  :license {:name "Eclipse Public License" :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/java.classpath "0.2.2"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]
                                  [org.webjars/font-awesome "4.3.0-1"]]
                   :plugins [[lein-midje "3.1.3"]]}})
