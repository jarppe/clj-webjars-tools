(ns example.hello-world
  (:require [org.httpkit.server :as http-kit]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [clj-webjars-tools.core :refer [webjar-resource]]))

(def index-page
  "<html>
     <head>
       <link rel=\"stylesheet\" href=\"/static/css/font-awesome.min.css\"/>
     </head>
     <body>
       <h1>Hello World <i class=\"fa fa-thumbs-o-up\"></i></h1>
     </body>
   </html>")

(defroutes app
  (GET "/" [] index-page)
  (webjar-resource "font-awesome" {:uri-root "/static"})
  (route/not-found "<h1>Page not found</h1>"))

(defn start-server []
  (http-kit/run-server #'app {:port 8080}))
