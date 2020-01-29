(ns simple-http-server.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]))

(def config-path "system.edn")


(defn read-config
  ([] (read-config :dev))
  ([profile]
   (assoc (aero/read-config (io/resource config-path) {:profile profile}) :env profile)))
