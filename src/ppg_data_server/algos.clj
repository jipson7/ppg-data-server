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

(defn apply-algo-to-data
  "Windowizes device data and applies the algos binary to its data"
  [data]
  (let [windowed (windowize-data data 100)
        red-windows (:red windowed)
        ir-windows (:ir windowed)]
    (assoc data :algos (map
        (fn [[red-window ir-window]]
          (let [red (map :y red-window)
                ir (map :y ir-window)
                timestamp (last (map :x red-window))
                out (get-led-stdout red ir)
                result (run-algos out)]
            (hash-map :x timestamp :y result)))
        (map vector red-windows ir-windows)))))

(defn apply-algo-to-trial
  [trial]
  (assoc trial :devices
         (map #(if (not (= "Ground Truth Sensor" (:type %)))
                 (update % :data apply-algo-to-data)
                 %)
              (:devices trial))))
