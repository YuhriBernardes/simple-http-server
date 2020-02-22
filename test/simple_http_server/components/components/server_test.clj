(ns simple-http-server.components.server-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [clojure.set :as clj-set]
   [cheshire.core :as json]
   [simple-http-server.config :as config]
   [integrant.core :as ig]
   [clj-http.client :as http-client]))

(defn init [config ks]
  (if (nil? ks)
    (ig/init config)
    (ig/init config ks)))

(defmacro with-system
  {:style/indent 1}
  [[binding config ks] & body]
  `(let [system#  (init ~config ~ks)
         ~binding system#]
     (try ~@body
          (finally (ig/halt! system#)))))

(def system-map (config/read-config-file :test))

(def server-host (str "http://localhost:" (get-in system-map [:server/config :port]) "/"))

(defn get-db-data [system]
  @(:database/config system))

(def rm-data-ids (partial map #(dissoc % :id)))

(defn  data->response [data]
  (map (fn [[k v]]
         (assoc v :id (str k))) (vec data)))

(deftest get-all-persons
  (with-system [sys system-map]
    (let [endpoint (str server-host "person")
          response (http-client/get endpoint)
          status   (:status response)
          body     (json/decode (:body response) keyword)]
    (testing "Status success"
      (is (< 199 status) 300))
    (testing "No data loss"
      (is (= (-> (get-db-data sys) data->response set)
             (set body)))))))
