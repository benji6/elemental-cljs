(ns elemental.view
    (:require [reagent.core :as reagent :refer [atom]]))

(defn view []
  [:div [:h1 "Elemental"]])

(defn mount-root []
  (reagent/render [view] (.getElementById js/document "app")))

(defn init! []
  (mount-root))
