(defproject integrity-api "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-http "0.5.3"]]
  :java-source-paths ["src/java-src"]
  :aot [integrity-api.integrity
        integrity-api.http
        integrity-api.core
        integrity-api.java-wrapper])
