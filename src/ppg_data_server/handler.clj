(ns ppg-data-server.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer [response status redirect]]
            [clojure.walk :refer [postwalk]]
            [ppg-data-server.algos :as algos]
            [ppg-data-server.data :as data]))

(defn gen-response [result]
  (if (nil? result)
    (status (response "Failed") 500)
    (response (str result))))

(defroutes app-routes
  (GET "/" [] (redirect "index.html"))
  (GET "/trials" [] (response (data/get-trials)))
  (GET "/trials/:id" [id] (response (algos/apply-algo-to-trial (data/get-trial id))))
  (POST "/trials" req
        (gen-response (data/save-trial (:body req))))
  (POST "/trials/:id" [id :as {data :body}]
        (gen-response (data/save-device id data)))
  (POST "/trials/:trial/devices/:device"
        [trial device :as {data :body}]
        (gen-response (data/save-data trial device data)))
  (route/resources "/")
  (route/not-found "Not Found"))

(defn stringify-ids [doc]
  (postwalk
   (fn [x]
     (if (and (map? x) (contains? x :_id))
       (update x :_id str) x))
   doc))

(defn wrap-string-ids
  [handler]
  (fn [request]
    (let [response (handler request)]
      (stringify-ids response))))

(def app
  (-> app-routes
      (wrap-defaults api-defaults)
      wrap-string-ids
      wrap-json-response
      wrap-json-body
      ))
