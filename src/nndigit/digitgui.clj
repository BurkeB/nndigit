(ns nndigit.digitgui
    (:use seesaw.core
          seesaw.color
          seesaw.graphics
          seesaw.behave)
    (:require [nndigit.classificator :as classificator])
    (:import java.awt.Color
             java.awt.image.BufferedImage
             javax.imageio.ImageIO
             java.io.File))


(def dotstyle (style :foreground "#000000"
                       :background "#000000"
                       :stroke (stroke :width 2)))

(defn draw-points [c g points]
     (let [width       (.getWidth c)
           height  (.getHeight c)
           m       (- (min width height) 15)
           r       (- (/ m 2) 10)]
        (doseq [p points]
            (draw g (circle (first p) (second p) 10) dotstyle))))

(defn get-time []
    (int (/ (.getTime (java.util.Date.)) 1000)))

(defn close-frame [e]
    (dispose! (select (to-root e) [:#frame]))) 

(defn save-image [e filename]
 (let [cvs (select (to-root e) [:#canvas])
       bi (new java.awt.image.BufferedImage (.getWidth cvs) (.getHeight cvs) java.awt.image.BufferedImage/TYPE_INT_RGB)
       g (.getGraphics bi)
       f (new java.io.File filename)]
      (.printAll cvs g)
      (javax.imageio.ImageIO/write bi "PNG" f))) 

(defn get-image [e]
 (let [cvs (select (to-root e) [:#canvas])
       bi (new java.awt.image.BufferedImage (.getWidth cvs) (.getHeight cvs) java.awt.image.BufferedImage/TYPE_INT_RGB)
       g (.getGraphics bi)]
      (.printAll cvs g)
      bi)) 

(defn create-canvas []
    (let [mouse-pressed (atom false)
          points (atom '())
          mouse-fn (fn [e] 
                       (swap! points conj [(.getX e) (.getY e)])
                       (repaint! (select (to-root e) [:#canvas])))
          cvs (canvas   :id :canvas
                        :background "#FFFFFF"
                        :paint #(draw-points %1 %2 (deref points))
                        :listen [:mouse-pressed (fn [e] (reset! mouse-pressed true) (mouse-fn e))
                                 :mouse-motion (fn [e] (when @mouse-pressed (mouse-fn e)))
                                 :mouse-released (fn [e] (reset! mouse-pressed false))])]                                 
        cvs))

(defn create-window []
    (-> (frame
           :id :frame 
           :title "Hello"
           :width 280
           :height 280
           :listen [:key-typed #(do (when-not (= (str (.getKeyChar %)) "x") 
                                     (save-image % (str (.getKeyChar %) "_" (get-time) ".png"))) 
                                    (close-frame %))] 
           :content (create-canvas)
           :on-close :exit)
     show!))

(defn create-input-window [ds classifier]
    (-> (frame
           :id :frame 
           :title "Hello"
           :width 280
           :height 280
           :listen [:key-typed #(do (when-not (= (str (.getKeyChar %)) "x")  
                                      (let [features (conj (classificator/get-image-feature (get-image %)) :0)] 
                                       (alert (-> features
                                               (classificator/classify ds classifier)))))
                                 (close-frame %))] 
           :content (create-canvas)
           :on-close :exit)
     show!))
