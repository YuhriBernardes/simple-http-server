(ns simple-http-server.interceptors.core
  (:require [simple-http-server.components.database :as database]
            [io.pedestal.log :as pedestal-log]))

(defn new-response [status body]
  {:status status
   :body   body})

(def ok (partial new-response 200))
(def created (partial new-response 201))

(def get-all-persons
  {:name  :get-all-persons
   :enter (fn [context]
            (let [db (:db context)
                  query-result (database/get-all db)]
              (assoc context :response (ok query-result))))})

(defn string->uuid [id]
  (java.util.UUID/fromString id))

(def get-person
  {:name :get-person
   :enter (fn [{:keys [request] :as context}]
            (let [db (:db context)
                  id (get-in request [:path-params :id])
                  query-result (database/find-entity db (string->uuid id))]
              (assoc context :response (ok query-result))))})
