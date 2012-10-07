(defproject java-integrity-api "1.0.0-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clj-http "0.5.3"]]
  :aot [java-integrity-api.integrity
        java-integrity-api.http
        java-integrity-api.core
        java-integrity-api.java-wrapper])
