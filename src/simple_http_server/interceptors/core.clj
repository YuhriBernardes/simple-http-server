(ns simple-http-server.interceptors.core
  (:require [simple-http-server.components.database :as database]))

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

(def create-person
  {:name :create-person
   :enter (fn [{:keys [request] :as context}]
            (let [db (:db context)
                  entity (get request :body)]
              (database/insert db [entity])
              (assoc context :response (created nil))))})

(def update-person
  {:name :update-person
   :enter (fn [{:keys [request] :as context}]
            (let [db (:db context)
                  id (get-in request [:path-params :id])
                  entity (-> (get request :body)
                             (dissoc :id))]
              (assoc context :response (ok (database/update-entity
                                            db
                                            (string->uuid id)
                                            #(merge % entity))))))})

(def delete-person
  {:name :delete-person
   :enter (fn [{:keys [request] :as context}]
            (let [db (:db context)
                  id (get-in request [:path-params :id])]
              (database/delete db [(string->uuid id)])
              (assoc context :response (ok nil))))})
