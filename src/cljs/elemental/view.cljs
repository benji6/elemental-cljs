(ns elemental.view
    (:require [reagent.core :as reagent :refer [atom]]))

(defn handle-touch-pad-input [e]
  (let [bounding-rect (.getBoundingClientRect (.-target e))
        x-offset (- (.-clientX e) (.-left bounding-rect))
        y-offset (- (.-clientY e) (.-top bounding-rect))]
    (println x-offset y-offset)))

(defn handle-touch-pad-input-stop [e]
    (println "leavin' pad"))

(defn view []
  [:div.center [:h1 "Elemental"]
        [:h3 "Instructions"]
        [:canvas {:on-mouse-move handle-touch-pad-input
                  :on-mouse-out handle-touch-pad-input-stop}]])

(defn mount-root []
  (reagent/render [view] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
