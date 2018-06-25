(ns ppg-data-server.data
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.result :refer :all]
            [monger.operators :refer :all])
  (:import [org.bson.types ObjectId]))

(def db
  (mg/get-db (mg/connect) "ppg"))

(def trial-coll "trials")

(defn set-id
  [id json]
  (merge {:_id id} json))

(defn save-trial
  "Returns a trial id or nil if saving fails"
  [json]
  (let [id (ObjectId.)
        document (set-id id json)]
    (if (acknowledged?
         (mc/insert db trial-coll document))
      id)))

(defn save-device
  "Saves a device to the given trial id"
  [trial-id json]
  (do
    (println (str "saving " json " to " trial-id))
    (let [id (ObjectId.)
          trial-oid (ObjectId. trial-id)
          document (set-id id json)]
      (if (updated-existing?
          (mc/update db trial-coll
                      {:_id trial-oid}
                      {$push {:devices document}}))
        id))))
