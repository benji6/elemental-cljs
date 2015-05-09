(enable-console-print!)

(ns elemental.keyboard
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<! >! put! chan]]))

(def key-codes-to-notes
  {:220 -10
    90 -9
    83 -8
    88 -7
    68 -6
    67 -5
    86 -4
    71 -3
    66 -2
    72 -1
    78 0
    74 1
    49 1
    77 2
    81 2
    87 3
    188 3
    51 4
    76 4
    69 5
    190 5
    52 6
    59 6
    82 7
    191 7
    84 8
    54 9
    89 10
    55 11
    85 12
    56 13
    73 14
    79 15
    48 16
    80 17
    173 18
    219 19
    221 20})


(def note-start-channel (chan))
(def note-stop-channel (chan))

(go (while true
    (let [note (<! note-start-channel)]
      (println "Note Start:" note))))

(go (while true
    (let [note (<! note-stop-channel)]
      (println "Note Stop:" note))))


(defn on-key-down [e]
  (let [key-code (.-keyCode e)]
    (let [note (key-codes-to-notes key-code)]
      (if note (go (>! note-start-channel note)))
      (if (= key-code 191) (.preventDefault e)))))

(defn on-key-up [e]
  (let [key-code (.-keyCode e)]
    (let [note (key-codes-to-notes key-code)]
      (if note (go (>! note-stop-channel note))))))

(set! (.-onkeydown js/document) on-key-down)
(set! (.-onkeyup js/document) on-key-up)
