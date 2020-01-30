(ns simple-http-server.core
  (:gen-class)
  (:require [integrant.core :as ig]
            [simple-http-server.config :as config]
            [simple-http-server.components.server]
            [simple-http-server.components.database]))

(defn -main [& args]
  (-> (config/read-config-file :prod)
      (ig/init)))
