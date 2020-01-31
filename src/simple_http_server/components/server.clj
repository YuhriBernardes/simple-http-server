(ns simple-http-server.components.server
  (:require [io.pedestal.http :as http]
            [integrant.core :as ig]
            [simple-http-server.interceptors.default :as default-interceptors]))

(defn new-service-map [{:keys [port server-type join? routes endpoint]}]
  (prn "creating service map")
  {::http/port port
   ::http/type server-type
   ::http/join? join?
   ::http/routes routes
   ::http/host endpoint})

(defn set-default-interceptors
  [service-map {:keys [db]}]
  (-> service-map
      (http/default-interceptors)
      (update ::http/interceptors conj (default-interceptors/database-interceptor db))
      (update ::http/interceptors conj default-interceptors/json-interceptor)))

(defmethod ig/init-key :server/config
  [_ {:keys [port routes server-type join? endpoint dependencies] :as config}]
  (let [service-map (new-service-map config)]
    (-> service-map
        (set-default-interceptors dependencies)
        http/create-server
        http/start)))

(defmethod ig/halt-key! :server/config
  [_ server]
  (http/stop server))
