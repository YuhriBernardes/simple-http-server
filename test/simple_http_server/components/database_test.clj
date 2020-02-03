(ns simple-http-server.components.database-test
  (:require [clojure.test :refer [deftest is testing]]
            [simple-http-server.components.database :as database]
            [clojure.set :as clj-set]))

(def mock-data [{:name "Jon Doe"
                 :age 20
                 :gender :male}
                {:name "Ana Ricker"
                 :age 20
                 :gender :female}])

(def db (database/new-database {}))

(deftest insert-entity
  (do
    (database/reset-database db)
    (let [db-entities (database/insert db mock-data)
          entity-maps (vals db-entities)]

      (testing "Entity count"
        (is (= (count mock-data) (count db-entities))))

      (testing "Assert inserted data"
        (is (= mock-data entity-maps))))))

(deftest find-entity
  (do
    (database/reset-database db)
    (let [db-entities        (database/insert db mock-data)
          entity-maps        (vals db-entities)
          entity-ids         (keys db-entities)
          searched-entity    (first entity-maps)
          searched-entity-id (first entity-ids)]

      (testing "Assert data"
        (is (= searched-entity (-> (database/find-entity db searched-entity-id)
                                   (dissoc :id))))))))

(deftest delete-entity
  (do
    (database/reset-database db)
    (let [db-entities     (database/insert db mock-data)
          entity-maps     (vals db-entities)
          entity-ids      (keys db-entities)
          deleted-entity-id (first entity-ids)
          db-after-delete (database/delete db [deleted-entity-id])
          after-delete-ids (keys db-after-delete)]

      (testing "Entity count"
        (is (> (count db-entities) (count db-after-delete))))

      (testing "Assert deleted data"
        (is (= #{deleted-entity-id} (clj-set/difference (set entity-ids) (set after-delete-ids))))))))

(deftest update-entity
  (do
    (database/reset-database db)
    (let [ db-entities          (database/insert db mock-data)
          entity-maps          (vals db-entities)
          entity-ids           (keys db-entities)
          before-update-entity (first entity-maps)
          updated-entity-id    (first entity-ids)
          updated-key          :age
          update-fn            #(update % updated-key inc)]

      (testing "Assert updated data"
        (is (= (update-fn before-update-entity)
               (-> (database/update-entity db updated-entity-id update-fn)
                   (dissoc :id))))))))
