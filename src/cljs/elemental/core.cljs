(enable-console-print!)

(ns elemental.core
    (:require [reagent.core :as reagent :refer [atom]]
      [elemental.keyboard :as keyboard]))

(defn calculate-note-frequency [n]
  (* 440 (.pow js/Math 2 (/ n 12))))

(println (calculate-note-frequency 12))

(def audio-context
  (js/AudioContext.))

(def osc
  (.createOscillator audio-context))

(.connect osc audio-context.destination)

(defn on-click []
  (.start osc))

(defn view []
  [:div [:h1 "Elemental"]
    [:button {:on-click on-click} "start/stop"]])

(defn mount-root []
  (reagent/render [view] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
