(ns ppg-data-server.data
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.result :refer [acknowledged? updated-existing?]]
            [monger.operators :refer [$push]]
            [clojure.walk :refer [postwalk]])
  (:import [org.bson.types ObjectId]))

(def db
  (mg/get-db (mg/connect) "ppg"))

(def trial-coll "trials")

(defn set-id
  [id json]
  (merge {:_id id} json))

(defn create-x-y-data-for-key
  "data set is a list of objects with data and data-key is the data to extract from the objects"
  [data-set data-key]
  (sort-by :x
   (map
    (fn [datum]
      {:x (:timestamp datum)
       :y (data-key datum)})
    data-set)))

(defn flatten-data
  [data]
  (reduce
   (fn [new-map key]
     (assoc new-map key (create-x-y-data-for-key data key)))
   {}
   (keys (first data))))

(defn get-trial
  "Returns a single trial with all nested data"
  [trial-id]
  (let [trial (mc/find-map-by-id db trial-coll (ObjectId. trial-id))
        devices (:devices trial)]
    (assoc trial :devices (map #(update % :data flatten-data) devices))))

(defn get-trials
  "Returns json of trials with metadata"
  []
  (mc/find-maps
  db
  trial-coll {}
  [:start :info :user :devices.name :devices.type]))

(defn get-trial-ids
  []
  (map :_id (get-trials)))

(defn get-device-info
  "Fetches a list of device ids and types from trial id (string)"
  [trial-id]
  (:devices
    (mc/find-one-as-map
    db
    trial-coll {:_id (ObjectId. trial-id)}
    [:devices._id :devices.type])))

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
    (let [id (ObjectId.)
          trial-oid (ObjectId. trial-id)
          document (set-id id json)]
      (if (updated-existing?
          (mc/update db trial-coll
                      {:_id trial-oid}
                      {$push {:devices document}}))
        id)))

(defn save-data
  "Saves a data snippet to given device and trial"
  [trial-id device-id json]
  (let [id (ObjectId.)
        trial-oid (ObjectId. trial-id)
        device-oid (ObjectId. device-id)
        document (set-id id json)]
    (if (updated-existing?
         (mc/update db trial-coll
                    {:_id trial-oid :devices._id device-oid}
                    {$push {:devices.$.data document}}))
      id)))

(defn save-algo-result
  "Saves result to existing collection"
  [result data-id device-id trial-id]
  (println result))

