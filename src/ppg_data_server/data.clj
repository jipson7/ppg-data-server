(ns ppg-data-server.data
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.result :refer [acknowledged?]])
  (:import [org.bson.types ObjectId]))

(def db-name "ppg")

(def trials-collection "trials")

(def db (mg/get-db (mg/connect) db-name))

(defn prep-data
  "Adds object ID to Map"
  [data]
  (merge {:_id (ObjectId.)} data))

(defn save [data]
  (acknowledged? (mc/insert
                  db
                  trials-collection
                  (prep-data data))))
    

