(ns simple-http-server.interceptors.default
  (:require [cheshire.core :as json]
            [io.pedestal.log :as pedestal-log]
            [io.pedestal.interceptor :as pedestal-interceptor]))

(def json-interceptor
  (pedestal-interceptor/map->Interceptor
   {:name  :json-interceptor
    :enter (fn [{:keys [request] :as context}]
             (pedestal-log/info :json-interceptor "Starded")
             (let [method        (get request :request-method)
                   request-body  (get request :body)
                   need-process? (some #(= % method) [:put :post])]
               (if need-process?
                 (->> (json/parse-string (slurp request-body) keyword)
                      (assoc-in context [:request :body]))
                 context)))
    :leave (fn [{:keys [response] :as context}]
             (let [response-body (get response :body)]
               (if (nil? response-body)
                 context
                 (assoc-in context [:response :body] (json/encode response-body)))))}))

(defn database-interceptor [db]
  (pedestal-interceptor/map->Interceptor
   {:name  :dependency-injector
    :enter (fn [context]
             (assoc context :db db))}))
