(ns elemental.core
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [cljs.core.async :refer [<! >! put! chan]]
      [elemental.audio-context :refer [audio-context]]
      [elemental.keyboard :refer [note-start-channel note-stop-channel]]
      [elemental.node-creators :refer [create-gain create-oscillator]]
      [elemental.view :refer [mount-root]]))

(enable-console-print!)

(defn init! [] (mount-root))

(def audio-graph (atom
  #{{:id 1
    :connect 0
    :creator create-gain
    :params 0.2}}))

(defn assoc-nodes [virtual-nodes]
    (map #(if (contains? % :node)
      %
      (assoc % :node ((% :creator) (% :params)))) virtual-nodes))

(defn connect-nodes! [virtual-nodes]
  (doall (for [virtual-node virtual-nodes]
    (let [parent-nodes (filter #(= (% :id) (virtual-node :connect)) virtual-nodes)
      child-node (virtual-node :node)
      parent-node (if (= 0 (count parent-nodes))
        (.-destination audio-context)
        ((nth parent-nodes 0) :node))]
      (.connect child-node parent-node)))) virtual-nodes)

(defn create-and-connect-nodes! [virtual-nodes]
  (connect-nodes! (assoc-nodes virtual-nodes)))

(defn disconnect-and-stop! [virtual-nodes]
  (doall (for [virtual-node virtual-nodes]
    (println (virtual-node :node)
    (.stop (virtual-node :node))))))

(swap! audio-graph #(create-and-connect-nodes! %))

(defn update-audio-graph! [new-graph]
  (let [nodes-to-add (remove (fn [new-node]
    (some (fn [old-node]
      (= (old-node :id) (new-node :id))) @audio-graph)) new-graph)
    nodes-to-remove (remove (fn [new-node]
      (some (fn [old-node]
        (= (old-node :id) (new-node :id))) new-graph)) @audio-graph)
    nodes-to-keep (filter (fn [new-node]
      (some (fn [old-node]
        (= (old-node :id) (new-node :id))) new-graph)) @audio-graph)]
    (disconnect-and-stop! (doall nodes-to-remove))
    (reset! audio-graph
      (create-and-connect-nodes! (doall (concat nodes-to-keep nodes-to-add))))))

(defn get-new-id []
  (+ (count @audio-graph) 1))

(defn play-freq! [freq]
  (update-audio-graph! (conj @audio-graph {:id (get-new-id)
      :connect 1
      :creator create-oscillator
      :params freq})))

(defn stop-freq! [freq]
  (println "remove" freq)
  (update-audio-graph! (remove #(= (% :params) freq) @audio-graph)))

(defn calculate-note-frequency [n]
  (* 440 (.pow js/Math 2 (/ n 12))))

(go (while true
  (let [note (<! note-start-channel)]
    (play-freq! (calculate-note-frequency note)))))

(go (while true
  (let [note (<! note-stop-channel)]
    (stop-freq! (calculate-note-frequency note)))))
