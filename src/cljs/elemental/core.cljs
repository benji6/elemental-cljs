(ns elemental.core
  (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [cljs.core.async :refer [<!]]
      [elemental.audio-context :refer [audio-context]]
      [elemental.keyboard :refer [note-start-channel note-stop-channel]]
      [elemental.channels :refer [note-start-channel note-stop-channel]]
      [elemental.node-creators :refer [create-gain create-oscillator]]
      [elemental.view :refer [mount-root]]))

(defn init! [] (mount-root))

(def audio-graph (atom
  #{{:id 1
     :connect 0
     :creator create-gain
     :params {:gain-value 0.2}}}))

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
    (.stop (virtual-node :node)))))

(defn diff-virtual-node [virtual-node]
  (println "diff logic goes here"))

(defn update-real-nodes! [virtual-nodes]
  (doall (for [virtual-node virtual-nodes]
    (diff-virtual-node virtual-node))))

(swap! audio-graph create-and-connect-nodes!)

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
    (disconnect-and-stop! nodes-to-remove)
    (update-real-nodes! nodes-to-keep)
    (reset! audio-graph
      (create-and-connect-nodes! (concat nodes-to-keep nodes-to-add)))))

(defn get-new-id []
  (+ (count @audio-graph) 1))

(defn calculate-frequency [pitch]
  (* 440 (.pow js/Math 2 (/ pitch 12))))

(defn play-note! [note]
  (let [{:keys [pitch mod]} note
        freq (calculate-frequency pitch)
        type (cond
               (< mod 0.25) "sawtooth"
               (< mod 0.5) "square"
               (< mod 0.75) "triangle"
               :else "sine")]
          (update-audio-graph! (conj @audio-graph {:id (get-new-id)
                                                   :connect 1
                                                   :creator create-oscillator
                                                   :params {:frequency freq
                                                            :type type}}))))

(defn stop-note! [note]
  (let [freq (calculate-frequency (note :pitch))]
    (update-audio-graph! (remove #(= (:frequency (% :params)) freq) @audio-graph))))

(go (while true
  (play-note! (<! note-start-channel))))

(go (while true
  (stop-note! (<! note-stop-channel))))
