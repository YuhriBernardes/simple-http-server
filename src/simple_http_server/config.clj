(ns simple-http-server.config
  (:require [aero.core :as aero]
            [clojure.java.io :as io]
            [integrant.core :as ig]))

(def config-path "system.edn")

(defmethod aero/reader 'ig/ref
  [_ _ value]
  (ig/ref value))

(defmethod aero/reader 'req-resolver
  [_ _ value]
  (var-get (requiring-resolve value)))

(defn read-config-file
  ([] (read-config-file :dev))
  ([profile]
   (aero/read-config (io/resource config-path) {:profile profile})))
