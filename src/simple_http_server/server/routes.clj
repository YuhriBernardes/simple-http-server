(ns simple-http-server.server.routes
  (:require [io.pedestal.http.route :as router]
            [io.pedestal.http :as http]
            [simple-http-server.interceptors.core :as interceptors]))


(def routes-set #{["/person" :get [interceptors/get-all-persons]]
                  #_["/person" :post [interceptors/get-all-persons]]
                  #_["/person" :put [interceptors/get-all-persons]]
                  #_["/person" :delete [interceptors/get-all-persons]]})

(def routes (router/expand-routes routes-set))

(comment)
