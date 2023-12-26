# publicize

Micro-library for publishing to clojars from deps.edn + tools.build.

This library is scoped to be for library authors which utilize
tools.build in their workflow.


## Overview

I wanted a more "bare-bones" library for publishing to clojars which
I can hook into `tools.build`. This is "bare-bones" as it get's, given
it is a single function which wraps `pomegranate` and provides some
basic checks against inputs.

If you need extra functionality, or want more of a "batteries included" solution, check out the Alternatives at the bottom of the README.


## Getting Started

### Installation
deps.edn
```
land.bnert/publicize {:mvn/version "0.3.0"}
```

### Usage
Add the following to your `build.clj` file (or the equivalent in your project):
```clojure
(ns build
  (:require
    [clojure.tools.build.api :as b]
    [publicize.core :as p]))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (clean nil)
  (let [b (b/create-basis {:project "deps.edn"})
        ; Optional, though recommended.
        ;
        ; Will clean clojure version from deps,
        ; which in turn will exclude the clojure version from the
        ; generated pom.xml
        b (p/clean-clojure-dep b)]
    (b/write-pom
      {:basis     basis
       :class-dir "target/classes"
       :lib       'myorg.mygroup/mylib
       :src-dirs  (get basis :paths ["src"])
       :version   "0.1.0"
       :pom-data  [[:licenses
                    [:license
                     [:name "Name of license"]
                     [:url  "https://..."]]]]}
       ; Optional
       :scm       {:url "https://..."})
    (b/copy-dir
      {:src-dirs   (get basis :paths ["src"])
       :target-dir "target/classes"})
    (b/jar
      {:class-dir class-dir
       :jar-file  "target/mylib.jar"})))

(defn publicize [_]
  (let [pom-path (b/pom-path
                   {:class-dir "", :lib 'myorg.mygroup/mylib})]
    (jar nil)
    (p/publicize
      {:lib      'myorg.mygroup/mylib
       :version  "0.1.0"
       :jar-file "target/mylib.jar"
       :pom-path pom-path})))
```

Then, call via cli:
```
$ CLOJARS_USERNAME=... CLOJARS_PASSWORD=... clj -T:build publicize
```

## Alternatives/Prior Work

- [`slipset/deps-deploy`](https://github.com/slipset/deps-deploy)
  - More of a "bundled" approach which handles jar packaging for you.
- [`applied-science/deps-library`](https://github.com/applied-science/deps-library)
  - Is a super-set of `deps-deploy` with some nifty features (if I am interpretting the README correctly). Check it out if you need more functionailty than what is here.

