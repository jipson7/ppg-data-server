(ns ppg-data-server.data
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.result :refer [acknowledged? updated-existing?]]
            [monger.operators :refer [$push]])
  (:import [org.bson.types ObjectId]))

(def db
  (mg/get-db (mg/connect) "ppg"))

(def trial-coll "trials")

(defn set-id
  [id json]
  (merge {:_id id} json))

(defn stringify-ids [docs]
  (map #(update % :_id str) docs))

(def device-data-keys
  {"Wrist Worn Device" '(:red :ir :accel :gyro :_id)
   "Ground Truth Sensor" '(:hr :hr_valid :oxygen :oxygen_valid :_id)
   "Fingertip Sensor" '(:red :ir :_id)})

(defn create-x-y-data-for-key
  "data set is a list of objects with data and data-key is the data to extract from the objects"
  [data-set data-key]
  (sort-by :x
   (map
    (fn [datum]
      {:x (:timestamp datum)
       :y (if (= data-key :_id)
            (str (data-key datum))
            (data-key datum))})
    data-set)))

(defn extract-all-data-params
  [device keys]
  (reduce
   (fn [new-map key]
     (assoc new-map key (create-x-y-data-for-key (:data device) key)))
   {}
   keys))

(defn extract-data-for-all-devices
  [doc]
  (reduce
   (fn [new-map device]
     (assoc new-map
            (:type device)
            (extract-all-data-params
             device
             (get device-data-keys (:type device)))))
   {}
   (:devices doc)))

(defn get-trials
  "Returns json of trials with metadata"
  []
  (stringify-ids
   (mc/find-maps
    db
    trial-coll {}
    [:start :info :user :devices.name :devices.type])))

(defn get-trial-ids
  []
  (map :_id (get-trials)))

(defn get-device-info
  "Fetches a list of device ids and types from trial id (string)"
  [trial-id]
  (stringify-ids
   (:devices
    (mc/find-one-as-map
    db
    trial-coll {:_id (ObjectId. trial-id)}
    [:devices._id :devices.type]))))

(defn get-trial
  "Returns a single trial with all nested data"
  [trial-id]
   (extract-data-for-all-devices (mc/find-map-by-id db trial-coll (ObjectId. trial-id))))

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
  [result data-id]
  (identity data-id))

