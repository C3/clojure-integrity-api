(ns integrity-api.java-wrapper
  (:require [integrity-api.core :as api]
            [clojure.walk])
  (:gen-class
    :name integrity_api.IntegrityJavaWrapper
    :implements [integrity_api.IIntegrityApi]))

(defn -login [this host user pass] (api/login host user pass))

(defn -searchAll [this integrity dataset] 
  (clojure.walk/stringify-keys (api/search (into {} integrity) dataset)))

(defn -search [this integrity dataset qualifiers]
  (clojure.walk/stringify-keys (api/search (into {} integrity) dataset (into {} qualifiers))))
