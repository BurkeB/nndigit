(ns nndigit.digitgui
    (:use seesaw.core
          seesaw.color
          seesaw.graphics
          seesaw.behave)
    (:import [java.awt.Color]))


(def dotstyle (style :foreground "#000000"
                       :background "#000000"
                       :stroke (stroke :width 2)))

(defn draw-points [c g points]
     (let [width       (.getWidth c)
        height  (.getHeight c)
        m       (- (min width height) 15)
        r       (- (/ m 2) 10)]
        (doseq [p points]
            (draw g (circle (first p) (second p) 10) dotstyle))
        ))

(defn create-canvas []
    (let [mouse-pressed (atom false)
          points (atom '([20 20] [120 120]))
          mouse-fn (fn [e] 
                        (swap! points conj [(.getX e) (.getY e)])
                        (repaint! (select (to-root e) [:#canvas])))
          cvs (canvas   :id :canvas
                        :background "#BBBBBB"
                        :paint #(draw-points %1 %2 (deref points))
                        :listen [:mouse-pressed (fn [e] (reset! mouse-pressed true) (mouse-fn e))
                                 :mouse-motion (fn [e] (when @mouse-pressed (mouse-fn e)))
                                 :mouse-released (fn [e] (reset! mouse-pressed false))])]
        cvs))

(defn create-window []
    (-> (frame :title "Hello"
           :width 500
           :height 500
           :content (create-canvas)
           :on-close :exit)
     show!))

