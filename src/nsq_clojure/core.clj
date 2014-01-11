(ns nsq-clojure.core
  (:require [nsq-clojure.connections :as connections]
            [nsq-clojure.commands :as cmd]))

(defn -main []
  (let [conn (connections/make-new-connection {:host "127.0.0.1" :port 4150})]
    (cmd/send-magic-identifier conn)
    (cmd/identify conn {})
    (cmd/sub conn "test-topic" "test-channel")
    (cmd/rdy conn 100)
    (cmd/mpub conn "test-topic" "WHAT UP" "MY HOMIES")))
