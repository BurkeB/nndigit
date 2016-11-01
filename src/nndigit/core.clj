(ns nndigit.core
  (:gen-class)
  (:require [nndigit.digitgui :as dg]
    [nndigit.classificator :as c]))          


(defn init-classification []
  (def ds (c/get-testdata "testdata/"))
  (def classificator (c/get-classifier ds)))
  

(defn input-window []
  (dg/create-input-window ds classificator))

(defn testdata-windows [n]
  (for [i (range n)]
   (dg/create-window)))
   

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
