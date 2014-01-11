(ns nsq-clojure.protocol
  (:require [clojure.string]
            [clojure.core.async :as async]
            [cheshire.core :as cheshire]
            [taoensso.nippy :as nippy]))

(def magic-identifier "  V2")

(defn ascii-from-long [long]
  (let [binary-string (format "%032d" (Long/parseLong (Long/toBinaryString long)))
        longs (map #(Long/parseLong (apply str %) 2) (partition 8 binary-string))]
    (apply str (map char longs))))

(defn long-from-ascii [string]
  (let [binary-string (->> (map #(Long/toBinaryString (int %)) string)
                           (map #(format "%08d" (Long/parseLong %)))
                           (apply str))]
    (Long/parseLong binary-string 2)))

(defn ascii-size-seq [message]
  (ascii-from-long (count message)))

(defn string-message [command & args]
  (str (clojure.string/join " " (cons command args)) "\n"))

(defn message-body-length [message]
  (let [ascii-size (take 4 message)]
    (long-from-ascii ascii-size)))

(defn json-message [command message]
  (let [json (cheshire/generate-string message)]
    (str command "\n" (ascii-size-seq json) json)))

(defn binary-message [command message]
  (let [binary-data (nippy/freeze message)]
    (str command "\n" (ascii-size-seq message) message)))

(defn multi-binary-message [command messages]
  (let [message-pair (fn [message] (apply str [(ascii-size-seq message) message]))]
    (str command "\n"
         (ascii-from-long (apply + (map count messages)))
         (ascii-size-seq messages)
         (apply str (flatten (map message-pair messages))))))

(defn parse-message [message-body]
  {:type :message
   :timestamp (long-from-ascii (apply str (take 8 message-body)))
   :attempts (long-from-ascii (apply str (take 2 (drop 8 message-body))))
   :message-id (apply str (take 16 (drop 10 message-body)))
   :message (apply str (drop 26 message-body))})

(defn dispatch-on-first-arg [first-arg & more] first-arg)
(defmulti process-message dispatch-on-first-arg)

(defmethod process-message :response [type out-channel message-body]
  (async/>!! out-channel {:type type
                          :message message-body}))

(defmethod process-message :error [type out-channel message-body]
  (async/>!! out-channel {:type type
                          :message message-body}))

(defmethod process-message :message [type out-channel message-body]
  (async/>!! out-channel (parse-message message-body)))

(defn dispatch-response [out-channel message]
  (let [frame-id (long-from-ascii (apply str (take 4 (drop 4 message))))
        message-body (apply str (drop 8 message))
        message-type (case frame-id
                       0 :response
                       1 :error
                       2 :message)]
    (process-message message-type out-channel message-body)))
