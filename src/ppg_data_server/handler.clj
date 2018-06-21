(ns ppg-data-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ppg-data-server.data :as data]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (POST "/echo" req
        (if (data/save (:body req))
          "Success"
          "Failure"))
  (route/not-found "Not Found"))


(def middleware
  (comp wrap-json-body wrap-defaults))

(def app
  (middleware app-routes api-defaults))
