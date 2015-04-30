(defproject clj-try "0.3.0"
  :description "clj-try: Try / Error macros"
  :url "https://github.com/martincooper/clj-try"
  :license {:name "Apache License Version 2.0"
            :url "http://www.apache.org/licenses/"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :min-lein-version "2.0.0"
  :uberjar-name "clj-try-standalone.jar"
  :profiles {:production {:env {:production true}}})
