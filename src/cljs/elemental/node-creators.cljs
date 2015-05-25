(ns elemental.node-creators
  (:require [elemental.audio-context :refer [audio-context]]))

(defn create-gain [params]
  (let [gain (.createGain audio-context)]
    (set! (.-value (.-gain gain)) (:gain-value params))
    gain))

(defn create-oscillator [params]
  (let [osc (.createOscillator audio-context)]
    (set! (.-value (.-frequency osc)) (:frequency params))
    (.start osc)
    osc))
