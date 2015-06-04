(ns elemental.view
    (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [reagent.core :as reagent :refer [atom]]
              [cljs.core.async :refer [>!]]
              [elemental.channels :refer [note-start-channel note-stop-channel]]))

(def last-note (atom {:pitch nil
                      :mod nil}))
(def pad-active (atom true))

(def major-scale [0 2 4 5 7 9 11 12])

(defn calculate-pitch [x total-x]
  ; (quot (* 12 x) total-x)) - chromatic
  (major-scale (quot (* 8 x) total-x)))

(defn send-new-note! [note]
  (if (not= nil @last-note)
    (go (>! note-stop-channel @last-note)))
  (go (>! note-start-channel (reset! last-note note))))

(defn handle-touch-pad-input! [e]
  (let [bounding-rect (.getBoundingClientRect (.-target e))
        x (- (.-clientX e) (.-left bounding-rect))
        y (- (.-clientY e) (.-top bounding-rect))
        width (- (.-right bounding-rect) (.-left bounding-rect))
        height (- (.-bottom bounding-rect) (.-top bounding-rect))
        pitch (calculate-pitch x width)]
          (if (not (and @pad-active (= pitch (@last-note :pitch))))
            (send-new-note! {:pitch pitch
                             :mod (/ y height)}))
          (reset! pad-active true))
    0) ;the 0 is a hack because chrome keeps warning returning false is
    ;deprecated but I don't care what this function returns and I want
    ;to be able to use the console

(defn handle-touch-pad-input-stop! [e]
  (reset! pad-active false)
  (go (>! note-stop-channel @last-note)))

(defn view []
  [:div [:div.center [:canvas {:on-mouse-move handle-touch-pad-input!
                               :on-mouse-out handle-touch-pad-input-stop!}]]
        [:div [:a.settings [:span]]]])

(defn mount-root []
  (reagent/render [view] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
