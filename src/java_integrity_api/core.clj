(ns java-integrity-api.core
  (:require [java-integrity-api.http :as service]
            [java-integrity-api.integrity :as i]))

(defn login [hostname user password]
  (let [session (i/mksession hostname user password)]
    (i/mkintegrity session (service/get-config session))))

(defn available-datasets [integrity]
  (map i/dataset-name (i/datasets (i/config integrity))))

(defn available-qualifiers [integrity dataset-name]
  (i/available-qualifiers integrity dataset-name))

(defn search
  ([integrity dataset-name] (search integrity dataset-name (available-qualifiers integrity dataset-name)))

  ([integrity dataset-name qualifiers]
   (let [dataset-id (i/dataset-id (i/find-dataset integrity dataset-name))
         path (str "datasets/" dataset-id "/search_results.xml")]

     (i/search-results
       (service/integrity-get (i/session integrity)
                              path
                              (i/qualifiers-to-params qualifiers))))))

