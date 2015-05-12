(ns elemental.node-creators
  (:require [elemental.audio-context :refer [audio-context]]))

; wanna remove this once virtual audio graph is working
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
