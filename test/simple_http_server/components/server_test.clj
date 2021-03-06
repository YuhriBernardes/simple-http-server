(ns simple-http-server.components.server-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [simple-http-server.components.server]
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
      (is (< 199 status 300)))
    (testing "No data loss"
      (is (= (-> (get-db-data sys) data->response set)
             (set body)))))))

(deftest get-person-by-id
  (with-system [sys system-map]
    (let [db-data (data->response (get-db-data sys))
          {:keys [id] :as entity} (first db-data)
          endpoint (str server-host "person/" id)
          response (http-client/get endpoint)
          status   (:status response)

          body (json/decode (:body response) keyword)]
      (testing "Status success"
        (is (< 199 status 300)))
      (testing "Is unique result"
        (is (map? body)))
      (testing "Find the person"
        (is (= entity
               body))))))

(deftest create-person
  (with-system [_sys system-map]
    (let [endpoint   (str server-host "person")
          new-entity {:name "new-person"
                      :age  20}
          response   (http-client/post endpoint {:body (json/encode new-entity)})
          status     (:status response)
          result     (http-client/get endpoint)
          body       (-> (:body result)
                         (json/decode keyword)
                         rm-data-ids)]
      (testing "Status success"
        (is (< 199 status 300)))
      (testing "Entity created"
        (is (seq (clj-set/intersection #{new-entity}
                                        (set body))))))))


(deftest update-person
  (with-system [sys system-map]
    (let [db-data (data->response (get-db-data sys))
          updated-entity (first db-data)
          updated-key (:id updated-entity)
          updated-values {:age 0}
          endpoint   (str server-host "person/" updated-key)
          response   (http-client/put endpoint {:body (json/encode updated-values)})
          status     (:status response)
          body       (-> (:body response)
                         (json/decode keyword)
                         rm-data-ids)]
      (testing "Status success"
        (is (< 199 status 300)))
      (testing "Entity updated"
        (is (not= updated-entity body))))))

(deftest delete-person
  (with-system [sys system-map]
    (let [db-data        (data->response (get-db-data sys))
          deleted-entity (first db-data)
          deleted-key    (:id deleted-entity)
          endpoint       (str server-host "person/" deleted-key)
          response       (http-client/delete endpoint)
          status         (:status response)
          result (http-client/get endpoint)
          body (-> (:body result)
                   (json/decode keyword))]
      (testing "Status success"
        (is (< 199 status 300)))
      (testing "Entity removed"
        (is (nil? body))))))
