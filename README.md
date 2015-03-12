# clj-webjars-tools

Clojure tool for serving WebJar content.

## Usage

This tool allows your ring application to serve static content from
WebJars. Main entry point is a function ```webjar-resource``` that
returns a ring handler.

Forexample, say you wan't to include awesome [Font Awesome](http://fontawesome.io) to your
new project. First, add [Font Awesome WebJar](https://github.com/webjars/font-awesome) and
this library to your ```project.clj```:

```clj
  [org.webjars/font-awesome "4.3.0-1"]
  [jarppe/clj-webjars-tools "1.0.0"]
```

Next, create a ring web app like this:

```clj
(ns example.hello-world
  (:require [org.httpkit.server :as http-kit]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [clj-webjars-tools.core :refer [webjar-resource]]))

(def index-page
  "<html>
     <head>
       <link rel=\"stylesheet\" href=\"/css/font-awesome.min.css\"/>
     </head>
     <body>
       <h1>Hello World <i class=\"fa fa-thumbs-o-up\"></i></h1>
     </body>
   </html>")

(defroutes app
  (GET "/" [] index-page)
  (webjar-resource "font-awesome")
  (route/not-found "<h1>Page not found</h1>"))

(defn start-server []
  (http-kit/run-server #'app {:port 8080}))
```

You propably wan't to limit the path to static resources by some prefix. So let's add ```/static```
to path:

```clj
(def index-page
  "<html>
     <head>
       <link rel=\"stylesheet\" href=\"/css/font-awesome.min.css\"/>
     </head>
     <body>
       <h1>Hello World <i class=\"fa fa-thumbs-o-up\"></i></h1>
     </body>
   </html>")
```

```clj
  (webjar-resource "font-awesome" {:uri-root "/static"})
```

You can also limit the WebJar content to some sub-path with ```:jar-root``` key.

## License

Copyright © 2015 Jarppe Länsiö

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
