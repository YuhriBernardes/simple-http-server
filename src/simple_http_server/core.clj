(ns simple-http-server.core
  (:gen-class)
  (:require [integrant.core :as ig]
            [simple-http-server.config :as config]
            [simple-http-server.components.server]
            [simple-http-server.routes]
            [simple-http-server.components.database]))

(defn -main
  [& args]
  (let [env (keyword (first args))
        config (config/read-config-file env)]
    (prn "Starting system with config:\n" config)
    (ig/init config)))
