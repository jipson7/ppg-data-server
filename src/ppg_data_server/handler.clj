(ns ppg-data-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.util.response :refer [response status]]
            [ppg-data-server.data :as data]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (POST "/trials" req
        (let [id (data/save (:body req))]
          (if (nil? id)
            (status (response "Failed") 500)
            (response (str id)))))
  (route/not-found "Not Found"))

(def middleware
  (comp wrap-json-body wrap-defaults))

(def app
  (middleware app-routes api-defaults))
