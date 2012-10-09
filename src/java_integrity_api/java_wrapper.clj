(ns java-integrity-api.java-wrapper
  (:require [java-integrity-api.core :as api]
            [clojure.walk])
  (:gen-class
    :name java_integrity_api.IntegrityJavaWrapper
    :implements [java_integrity_api.IIntegrityApi]))

(defn -login [this host user pass] (api/login host user pass))

(defn -searchAll [this integrity dataset] 
  (clojure.walk/stringify-keys (api/search (into {} integrity) dataset)))

(defn -search [this integrity dataset qualifiers]
  (clojure.walk/stringify-keys (api/search (into {} integrity) dataset (into {} qualifiers))))
