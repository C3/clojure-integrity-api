(ns java-integrity-api.java-wrapper
  (:require [java-integrity-api.core :as api])
  (:gen-class
    :name java-integrity-api.java-wrapped
    :methods [#^{:static true} [login [String String String] Object]]))

(defn -login [host user pass] (api/login host user pass))
