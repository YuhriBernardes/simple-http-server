(ns simple-http-server.routes
  (:require [io.pedestal.http.route :as router]
            [simple-http-server.interceptors.core :as interceptors]))


(def routes-set #{["/person" :get [interceptors/get-all-persons]]
                  ["/person" :post [interceptors/create-person]]
                  ["/person/:id" :get [interceptors/get-person]]
                  ["/person/:id" :put [interceptors/update-person]]
                  ["/person/:id" :delete [interceptors/delete-person]]})

(def routes (router/expand-routes routes-set))
