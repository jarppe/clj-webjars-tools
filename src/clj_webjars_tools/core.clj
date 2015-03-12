(ns clj-webjars-tools.core
  (:require [clojure.java.io]
            [clojure.java.classpath :as cp])
  (:import [java.io File FilenameFilter]
           [java.util.jar JarFile JarEntry]))

(set! *warn-on-reflection* true)

(def webjars-path-prefix "META-INF/resources/webjars/")

(defn- ->jarfile ^JarFile [^File f]
  (if (.isFile f) (JarFile. f false)))

(defn- contains-webjar? [artifact-name]
  (let [path-name (str webjars-path-prefix artifact-name)]
    (fn [^JarFile jar]
      (some (fn [^JarEntry f]
              (-> f (.getName) (.startsWith path-name)))
            (enumeration-seq (.entries jar))))))

(defn find-webjar ^JarFile [artifact-name]
  (let [jars (->> (cp/classpath)
                  (keep ->jarfile)
                  (filter (contains-webjar? artifact-name)))
        [jar & other-versions] jars]
    (if-not jar
      (throw (java.io.IOException. (str "WebJars error: Can't find WebJar for '" artifact-name "'"))))
    (if other-versions
      (throw (java.io.IOException. (str "WebJars error: Multiple versions for WebJar '" artifact-name "' found"))))
    jar))

(defn find-webjar-version [artifact-name ^JarFile jar]
  (let [p (re-pattern (str webjars-path-prefix artifact-name "\\/([^\\/]+)\\/.*"))
        versions (->> jar
                      (.entries)
                      (enumeration-seq)
                      (keep (fn [^JarEntry e]
                              (->> e (.getName) (re-matches p) (second))))
                      (set))]
    (if-not (= (count versions) 1)
      (throw (java.io.IOException. (str "WebJars error: Can't detect version from '" artifact-name "' WebJar"))))
    (first versions)))

(defn find-resource ^JarEntry [^JarFile jar artifact-name version resource-name]
  (let [full-name (str webjars-path-prefix artifact-name "/" version resource-name)]
    (->> jar
         (.entries)
         (enumeration-seq)
         (some (fn [^JarEntry e]
                 (if (->> e (.getName) (= full-name)) e))))))

(defn webjar-resource
  "Returns ring handler that servers resources from WebJar. 'artifact-name' is the
  WebJar artifact name (for example \"font-awesome\"). If the WebJar is not found
  from classpath throws java.io.IOException.

  Optionally accepts an options map. Supported entries are:
    :uri-root   Prefix for request URI. Request must start with this prefix.
    :jar-root   Prefix for paths inside WebJar.

  Example: Serve request starting with \"/static/fonts\" from FontAwesome WebJar:
    (webjar-resource \"font-awesome\" {:uri-root \"/static/fonts\" :resource-root \"/fonts\"})"
  [artifact-name & [{:keys [uri-root jar-root] :or {uri-root "" jar-root ""}}]]
  (let [jar           (find-webjar artifact-name)
        version       (find-webjar-version artifact-name jar)
        uri-root-len  (count uri-root)]
    (fn [{:keys [request-method ^String uri] :as request}]
      (if (and (= request-method :get)
               (.startsWith uri uri-root))
        (if-let [entry (find-resource jar artifact-name version (str jar-root (.substring uri uri-root-len)))]
          {:status 200
           :body (.getInputStream jar entry)})))))
