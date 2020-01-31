(ns package
  (:refer-clojure :exclude [compile])
  (:require [badigeon.bundle :refer [bundle make-out-path]]
            [badigeon.compile :refer [compile]]))

(defn -main []
  (bundle (make-out-path 'lib nil) {:allow-unstable-deps? true})
  (compile 'simple-http-server.core {:compile-path "target/classes"}))
