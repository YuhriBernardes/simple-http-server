(ns simple-http-server.server.routes
  (:require [io.pedestal.http.route :as router]
            [simple-http-server.server.interceptors :as interceptors]))


(def routes-set #{["/person" :get [interceptors/get-all-persons]]
                  ["/person" :post [interceptors/get-all-persons]]
                  ["/person" :put [interceptors/get-all-persons]]
                  ["/person" :delete [interceptors/get-all-persons]]})

(defn inject-defautl-interceptors [router-set default-interceptors]
  (let [update-fn (partial concat default-interceptors)]
    (map #(update % 2 update-fn) router-set)))
