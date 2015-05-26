(ns elemental.channels
  (:require [cljs.core.async :refer [chan]]))

(def note-start-channel (chan))
(def note-stop-channel (chan))
