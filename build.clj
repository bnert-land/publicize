(ns build
  (:require
    [clojure.pprint :refer [pprint]]
    [clojure.tools.build.api :as b]
    [publicize.core :as p]))

(def lib 'land.bnert/publicize)
(def version "0.4.0")
(def target-dir "target")
(def class-dir (str target-dir "/classes"))
(def pom-data
  [[:licenses
    [:license
     [:name "Eclipse Public License version 2.0"]
     [:url  "https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt"]
     [:distribution "repo"]]]])

(defn clean [_]
  (println "Cleaning...")
  (b/delete {:path "target"})
  (println "Done.")
  {})

(defn basis [_]
  (let [b (p/clean-clojure-dep (b/create-basis {:project "deps.edn"}))]
    (println (keys b))
    (pprint
      (:deps b))
    (pprint
      (:libs b))))

(defn jar [_]
  (clean nil)
  (let [basis    (p/clean-clojure-dep (b/create-basis {:project "deps.edn"}))
        jar-path (format (str target-dir "/%s-%s.jar")
                         (name lib)
                         version)]
    (println "Writing pom...")
    (b/write-pom
      {:basis     basis
       :class-dir class-dir
       :lib       lib
       :pom-data  pom-data
       :src-dirs  (get basis :paths ["src"])
       :scm       {:url "https://github.com/bnert-land/publicize"}
       :version   version})
    (println "Copying src...")
    (b/copy-dir
      {:src-dirs   (get basis :paths ["src"])
       :target-dir class-dir})
    (println "Building jar...")
    (b/jar
      {:class-dir class-dir
       :jar-file  jar-path})
    (println "Done.")
    {:jar-file jar-path
     :lib      lib
     :pom-file (b/pom-path {:lib lib, :class-dir class-dir})
     :version  version}))

(defn publicize [_]
  (let [result (jar nil)]
    (println "Publicizing...")
    (p/publicize (into result
                       {:username (System/getenv "CLOJARS_USERNAME")
                        :password (System/getenv "CLOJARS_PASSWORD")}))
    (println "Done.")))

