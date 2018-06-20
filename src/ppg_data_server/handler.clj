(ns ppg-data-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (POST "/echo" req (str req))
  (route/not-found "Not Found"))

(def middleware
  (comp wrap-json-body wrap-defaults))

(def app
  (middleware app-routes api-defaults))
