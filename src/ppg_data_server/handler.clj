(ns ppg-data-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [ring.util.response :refer [response status resource-response]]
            [ppg-data-server.data :as data]))

(defn gen-response [result]
  (if (nil? result)
    (status (response "Failed") 500)
    (response (str result))))

(defroutes app-routes
  (GET "/" [] (resource-response "index.html"))
  (POST "/trials" req
        (let [trial-id (data/save-trial (:body req))]
          (gen-response trial-id)))
  (POST "/trials/:id" [id :as {data :body}]
        (let [device-id (data/save-device id data)]
          (gen-response device-id)))
  (POST "/trials/:trial/devices/:device"
        [trial device :as {data :body}]
        (let [data-id (data/save-data trial device data)]
          (gen-response data-id)))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn wrap-dir-index [handler]
  (fn [req]
    (handler
     (update-in req [:uri]
                #(if (= "/" %) "/index.html" %)))))

(def middleware
  (comp wrap-dir-index wrap-json-body wrap-defaults))

(def app
  (middleware app-routes api-defaults))
