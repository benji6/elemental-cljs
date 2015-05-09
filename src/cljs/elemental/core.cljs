(enable-console-print!)

(ns elemental.core
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [cljs.core.async :refer [<! >! put! chan]]
      [reagent.core :as reagent :refer [atom]]
      [elemental.keyboard :refer [note-start-channel note-stop-channel]]))

(defn calculate-note-frequency [n]
  (* 440 (.pow js/Math 2 (/ n 12))))

(def audio-context (js/AudioContext.))
(def osc (.createOscillator audio-context))

(.connect osc audio-context.destination)

(go (while true
    (let [note (<! note-start-channel)]
      (println "Reading Note Start:" (calculate-note-frequency note)))))

(go (while true
    (let [note (<! note-stop-channel)]
      (println "Reading Note Stop:" (calculate-note-frequency note)))))

(defn on-click []
  (.start osc))

(defn view []
  [:div [:h1 "Elemental"]
    [:button {:on-click on-click} "start/stop"]])

(defn mount-root []
  (reagent/render [view] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
