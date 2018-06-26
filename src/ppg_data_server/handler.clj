(ns ppg-data-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer [response status redirect]]
            [ppg-data-server.data :as data]))

(defn gen-response [result]
  (if (nil? result)
    (status (response "Failed") 500)
    (response (str result))))

(defroutes app-routes
  (GET "/" [] (redirect "index.html"))
  (GET "/trials" [] (response (data/get-trials)))
  (POST "/trials" req
        (gen-response (data/save-trial (:body req))))
  (POST "/trials/:id" [id :as {data :body}]
        (gen-response (data/save-device id data)))
  (POST "/trials/:trial/devices/:device"
        [trial device :as {data :body}]
        (gen-response (data/save-data trial device data)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def middleware
  (comp wrap-json-body wrap-json-response wrap-defaults))

(def app
  (middleware app-routes api-defaults))
