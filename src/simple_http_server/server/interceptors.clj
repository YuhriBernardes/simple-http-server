(ns simple-http-server.server.interceptors
  (:require [simple-http-server.components.database :as database]
   #_[io.pedestal.interceptor :as pedestal-interceptors]))

(defn new-response [status body]
  {:status status
   :body body})

(def ok (partial 200))
(def created (partial 201))

(defn dependency-injector [dependencies]
  {:name :dependency-injector
   :enter (fn [context]
            (merge context dependencies))})

(def get-all-persons
  {:name :get-all-persons
   :enter (fn [context]
            (let [db (:db context)]
              (-> context
                  (assoc :response (database/get-all db)))))})

(defn default-interceptors [dependencies]
  [(dependency-injector dependencies)])
