(ns nsq-clojure.commands
  (:require [nsq-clojure.connections :as conns]
            [nsq-clojure.protocol :as protocol]))

(defn send-magic-identifier [conn]
  (conns/write-to-conn conn protocol/magic-identifier))

(defn identify [conn spec]
  (conns/write-to-conn conn (protocol/json-message "IDENTIFY" spec)))

(defn sub [conn topic-name channel-name]
  (conns/write-to-conn conn (protocol/string-message "SUB" topic-name channel-name)))

(defn pub [conn topic-name message]
  (conns/write-to-conn conn (protocol/binary-message (str "PUB " topic-name) message)))

(defn mpub [conn topic-name & messages]
  (conns/write-to-conn conn (protocol/multi-binary-message (str "MPUB " topic-name) messages)))

(defn rdy [conn n]
  (conns/write-to-conn conn (protocol/string-message "RDY" n)))

(defn fin [conn message-id]
  (conns/write-to-conn conn (protocol/string-message "FIN" message-id)))

(defn req [conn message-id timeout]
  (conns/write-to-conn conn (protocol/string-message "REQ" message-id timeout)))

(defn touch [conn message-id]
  (conns/write-to-conn conn (protocol/string-message "TOUCH" message-id)))

(defn cls [conn]
  (conns/write-to-conn conn (protocol/string-message "CLS")))

(defn nop [conn]
  (conns/write-to-conn conn (protocol/string-message "NOP")))
