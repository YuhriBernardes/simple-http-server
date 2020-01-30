(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [simple-http-server.config :as config]))

(def config-map (config/read-config-file))

(ig-repl/set-prep! (constantly config-map))
(ig-repl/prep)

(defn reset []
  (ig-repl/halt)
  (ig-repl/go))

(comment
  (ig-repl/go)
  (ig-repl/halt)
  (reset)
  )
