(ns elemental.core
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [cljs.core.async :refer [<! >! put! chan]]
      [reagent.core :as reagent :refer [atom]]
      [elemental.keyboard :refer [note-start-channel note-stop-channel]]))

(enable-console-print!)

(def audio-context (js/AudioContext.))

(def active-notes (atom {}))

(defn create-gain [gain-value]
  (let [gain (.createGain audio-context)]
    (set! (.-value (.-gain gain)) gain-value)
    gain))

(defn create-oscillator [freq]
  (let [osc (.createOscillator audio-context)]
    (set! (.-value (.-frequency osc)) freq)
    (.start osc)
    (swap! active-notes #(let [freq-oscillators (% freq)]
      (assoc % freq (if freq-oscillators
        (conj freq-oscillators osc)
        #{osc})))) osc))

(def audio-graph
  #{{:id 1
      :connect 0
      :creator create-gain
      :params 0.2}
    {:id 2
      :connect 1
      :creator create-oscillator
      :params 800}})

(defn assoc-nodes [graph]
  (map #(assoc % :node ((% :creator) (% :params))) graph))

(defn connect-nodes [graph]
  (doall (for [virtual-node graph]
    (let [parent-nodes (filter #(= (% :id) (virtual-node :connect)) graph)
      child-node (virtual-node :node)
      parent-node (if (= 0 (count parent-nodes))
        (.-destination audio-context)
        ((nth parent-nodes 0) :node))]
    (.connect child-node parent-node)))))

(connect-nodes (assoc-nodes audio-graph))

(defn play-freq! [freq]
  (let [osc (create-oscillator freq)
    gain (create-gain 0.5)]
    (.connect osc gain)
    (.connect gain audio-context.destination)))

(defn stop-freq! [freq]
  (doall (for [osc (@active-notes freq)] (.stop osc)))
    (swap! active-notes #(dissoc % freq)))

(defn calculate-note-frequency [n]
  (* 440 (.pow js/Math 2 (/ n 12))))

(go (while true
    (let [note (<! note-start-channel)]
      (play-freq! (calculate-note-frequency note)))))

(go (while true
    (let [note (<! note-stop-channel)]
    (stop-freq! (calculate-note-frequency note)))))

(defn view []
  [:div [:h1 "Elemental"]])

(defn mount-root []
  (reagent/render [view] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
