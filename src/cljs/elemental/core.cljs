(ns elemental.core
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [cljs.core.async :refer [<! >! put! chan]]
      [reagent.core :as reagent :refer [atom]]
      [elemental.keyboard :refer [note-start-channel note-stop-channel]]))

(enable-console-print!)

(def active-notes (atom {}))

(defn calculate-note-frequency [n]
  (* 440 (.pow js/Math 2 (/ n 12))))

(def audio-context (js/AudioContext.))

(defn play-freq [freq]
  (let [osc (.createOscillator audio-context)]
    (set! (.-value (.-frequency osc)) freq)
    (.connect osc audio-context.destination)
    (.start osc)
    (swap! active-notes #(let [freq-oscillators (% freq)]
      (assoc % freq (if freq-oscillators
        (conj freq-oscillators osc)
        #{osc}))))
    (println active-notes)))

(defn stop-freq [freq]
  (doall (for [osc (@active-notes freq)] (.stop osc)))
    (swap! active-notes #(dissoc % freq)))

(go (while true
    (let [note (<! note-start-channel)]
      (play-freq (calculate-note-frequency note)))))

(go (while true
    (let [note (<! note-stop-channel)]
    (stop-freq (calculate-note-frequency note)))))

(defn view []
  [:div [:h1 "Elemental"]])

(defn mount-root []
  (reagent/render [view] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
