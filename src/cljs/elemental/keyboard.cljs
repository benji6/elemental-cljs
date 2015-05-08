(enable-console-print!)

(ns elemental.keyboard)

(defn on-key-down [e]
  (let [key-code (.-keyCode e)]
    (println key-code)
    (if (= key-code 191)
      (.preventDefault e))))

(set! (.-onkeydown js/document) on-key-down)
