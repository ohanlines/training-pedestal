(ns validate
  (:require [struct.core :as st]))

(def schema
  [[:username st/required st/string
    {:message "username must contain at least 3 characters"
     :validate (fn [msg] (>= (count msg) 3))}]])

(defn validate-name [username param]
  (let [val (st/validate username schema)]
    (case param
      :error (first val)
      :value (second val))))