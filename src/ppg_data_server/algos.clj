(ns ppg-data-server.algos
  (:require [ppg-data-server.data :as data]
            [me.raynes.conch :as sh]
            [clojure.data.json :as json])
  (:use [clojure.string :only [join]] ))

(defn windowize-data
  "Take the x-y'd device data and creates sliding windows"
  [data window-size]
  (reduce
   (fn [new-map [k v]]
     (assoc new-map k
            (partition window-size 1 v)))
   {}
   (seq data)))

(defn run-algos
  "Runs the algos binary, returns a map"
  [input]
  (sh/let-programs [run-algos "./bin/algos"]
    (json/read-str (run-algos {:in input}) :key-fn keyword)))

(defn get-led-stdout
  "Creates the output to send to algos binary"
  [red ir]
  (str (join "," red) " " (join "," ir)))

(defn apply-algo-to-device
  "Windowizes device data and applies the algos binary to its data"
  [device trial-id]
  (let [device-id (:_id device)
        data (:data device)
        windowed (windowize-data data 100)
        red-windows (:red windowed)
        ir-windows (:ir windowed)
        id-windows (:_id windowed)]
    (first
      (map
        (fn [[red-window ir-window id-window]]
          (let [red (map :y red-window)
                ir (map :y ir-window)
                data-id (:y (last id-window))
                out (get-led-stdout red ir)
                result (run-algos out)]
            (data/save-algo-result result data-id device-id trial-id)))
        (map vector red-windows ir-windows id-windows)))))

(defn apply-algo-to-trial
  [trial]
  (doall
   (map
    (fn [device]
      (if (not (= "Ground Truth Sensor" (:type device)))
        (apply-algo-to-device device (:_id trial))))
    (:devices trial))))
