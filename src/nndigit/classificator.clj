(ns nndigit.classificator
  (:require [mikera.image.core :as ic]
            [mikera.image.colours :as icolours]
            [clj-ml.data :as mld]
            [clj-ml.classifiers :as mlc]))          
             
(defn get-grayvalue [int-color]
  (double (/ (/ (+ (icolours/extract-red int-color)
                 (icolours/extract-green int-color)
                 (icolours/extract-blue int-color)) 3) 255)))

(defn get-image-feature [img]
 (let [img (ic/resize img 26 24)
       pixels (ic/get-pixels img)]
  (map get-grayvalue pixels)))

(defn get-file-feature [filename]
 (let [img (ic/load-image filename)]
     (get-image-feature img)))
  
(defn get-fileinfo [filename]
  {:filename filename :digit (first (clojure.string/split filename #"\_"))})

(defn get-testdata [folder]
  (mld/make-dataset "digits" (conj (map #(keyword (str "xy" %)) (range 624)) 
                                   {:kind [:0 :1 :2 :3 :4 :5 :6 :7 :8 :9]})
   (map #(let [info (get-fileinfo %)]    
           (conj (get-file-feature (str folder (:filename info))) (keyword (str (:digit info))))) (.list (clojure.java.io/file folder)))))

(defn get-classifier [dataset]
  (mld/dataset-set-class dataset 0)
  (-> 
    (mlc/make-classifier :neural-network :multilayer-perceptron)
    (mlc/classifier-train dataset)))


(defn round-prediction [number]
  (read-string (clojure.string/replace (format "%.4f" number) #"," ".")))

(defn classify [grays dataset classifier]
    (let [instance (mld/make-instance dataset grays)
          prediction (.distributionForInstance classifier instance)] 
     (reverse (sort-by :probability 
               (for [i (mld/dataset-class-labels dataset)]
                {:class (first i)
                 :probability (round-prediction (get prediction (second i)))
                 :classification (mlc/classifier-classify classifier instance)}))))) 

     