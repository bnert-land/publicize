(ns publicize.core
  (:require
    [cemerick.pomegranate.aether :as aether]))

(defn publicize
  [{:keys [lib version username password jar-file pom-file]}]
  (assert lib "':lib' must be provided. Example: myorg.mygroup/mylib")
  (assert jar-file "':jar-file' must be provided.")
  (assert pom-file "':pom-file' must be provided.")
  (assert version "':version' must be provided.")
  (aether/deploy
    :coordinates [lib version]
    :jar-file    jar-file
    :pom-file    pom-file
    :repository
    {"clojars"
      {:url "https://repo.clojars.org"
       :username (or username (System/getenv "CLOJARS_USERNAME"))
       :password (or password (System/getenv "CLOJARS_PASSWORD"))}}))

(defn clean-clojure-dep [basis]
  (-> basis
      (update :deps dissoc 'org.clojure/clojure)
      (update :libs dissoc 'org.clojure/clojure)))

