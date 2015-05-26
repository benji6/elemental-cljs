(ns elemental.view
    (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [reagent.core :as reagent :refer [atom]]
              [cljs.core.async :refer [>!]]
              [elemental.channels :refer [note-start-channel note-stop-channel]]))

(defn handle-touch-pad-input [e]
  (let [bounding-rect (.getBoundingClientRect (.-target e))
        x-offset (- (.-clientX e) (.-left bounding-rect))
        y-offset (- (.-clientY e) (.-top bounding-rect))]
    (go (>! note-stop-channel 0))
    (go (>! note-start-channel 0))
    (println x-offset y-offset)))

(defn handle-touch-pad-input-stop [e]
  (go (>! note-stop-channel 0)))

(defn view []
  [:div.center [:h1 "Elemental"]
        [:h3 "Instructions"]
        [:canvas {:on-mouse-move handle-touch-pad-input
                  :on-mouse-out handle-touch-pad-input-stop}]])

(defn mount-root []
  (reagent/render [view] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
