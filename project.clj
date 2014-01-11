(defproject nsq-clojure "0.1.0-SNAPSHOT"
  :description "NSQ client using core.async"
  :url "https://github.com/thieman/nsq-clojure"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main nsq-clojure.core
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [com.taoensso/nippy "2.5.2"]
                 [cheshire "5.3.1"]])
