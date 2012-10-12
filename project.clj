(defproject integrity-api "0.1.0"
  :description "Java/Clojure API for the Integrity web service"
  :url "http://github.com/c3/clojure-integrity-api"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-http "0.5.3"]]
  :java-source-paths ["src/java-src"]
  :aot [integrity-api.integrity
        integrity-api.http
        integrity-api.core
        integrity-api.java-wrapper])
