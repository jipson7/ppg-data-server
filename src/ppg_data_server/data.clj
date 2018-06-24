(ns ppg-data-server.data
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.result :refer [acknowledged?]])
  (:import [org.bson.types ObjectId]))

(def db (mg/get-db (mg/connect) "ppg"))

(defn save
  "Returns an id or nil if saving fails"
  [data]
  (let [id (ObjectId.)
        document (merge {:_id id} data)]
    (if (acknowledged? (mc/insert db "trials" document)) id)))
