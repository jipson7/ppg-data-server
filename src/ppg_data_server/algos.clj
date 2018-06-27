(ns ppg-data-server.algos
  (:require [ppg-data-server.data :as data])
  (:use [clojure.string :only [join]] ))

(defn extract-data-points
  [data key]
  (join "," (map :y (take 100 (key data)))))

(defn get-led-samples
  "Returns 100 readings from each LED, in first trial"
  []
  (let [trial-id (:_id (first (data/get-trials)))
        trial (data/get-trial trial-id)
        data (get trial "Wrist Worn Device")]
    (hash-map :red (extract-data-points data :red)
              :ir (extract-data-points data :ir))))

(defn get-led-stdout
  []
  (let [led-samples (get-led-samples)]
    (str (:red led-samples) " " (:id led-samples))))
