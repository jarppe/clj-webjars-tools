(ns clj-webjars-tools.core-test
  (:require [midje.sweet :refer :all]
            [clj-webjars-tools.core :refer :all]))

; dev resources include WebJar [org.webjars/font-awesome "4.3.0-1"].

(facts find-webjar
  (find-webjar "font-awesome")  => truthy
  (find-webjar "foo")           => (throws java.io.IOException))

(facts find-webjar-version
  (find-webjar-version "font-awesome" (find-webjar "font-awesome"))  => "4.3.0"
  (find-webjar-version "foo" (find-webjar "font-awesome"))           => (throws java.io.IOException))

(defn ok? [{:keys [status body]}]
  (and (= status 200)
       (instance? java.io.InputStream body)))

(facts find-resource
  (let [handler (webjar-resource "font-awesome")]
    (handler {:request-method :get  :uri "/fonts/FontAwesome.otf"})  => ok?
    (handler {:request-method :post :uri "/fonts/FontAwesome.otf"})  => nil
    (handler {:request-method :get  :uri "/fonts/foo"})              => nil))

(facts find-resource
  (let [handler (webjar-resource "font-awesome" {:jar-root "/fonts"})]
    (handler {:request-method :get  :uri "/FontAwesome.otf"})              => ok?)
  (let [handler (webjar-resource "font-awesome" {:uri-root "/static"})]
    (handler {:request-method :get :uri "/static/fonts/FontAwesome.otf"})  => ok?)
  (let [handler (webjar-resource "font-awesome" {:uri-root "/static" :jar-root "/fonts"})]
    (handler {:request-method :get :uri "/static/FontAwesome.otf"})        => ok?))
