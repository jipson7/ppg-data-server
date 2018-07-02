(ns ppg-data-server.algos
  (:require [ppg-data-server.data :as data]
            [me.raynes.conch :as sh]
            [clojure.data.json :as json])
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
  "Creates the output to send to algos binary"
  [led-window]
  (str (:red led-window) " " (:ir led-window)))

(defn run-algos
  "Runs the algos binary, returns a map"
  [input]
  (sh/let-programs [run-algos "./bin/algos"]
    (json/read-str (run-algos {:in input}) :key-fn keyword)))

