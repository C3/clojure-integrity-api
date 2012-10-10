(ns integrity-api.core
  (:require [integrity-api.http :as service]
            [integrity-api.integrity :as i]))

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
         path (str "datasets/" dataset-id "/search_results.xml")
         results-per-page 200
         session (i/session integrity)
         params (assoc (i/qualifiers-to-params qualifiers) :per_page results-per-page)]

     (letfn [(get-results [page-num]
               (lazy-seq
                 (let [results (i/search-results
                                 (service/integrity-get session path (assoc params :page page-num)))]
                   (if (< (count results) results-per-page)
                     results
                     (concat results (get-results (inc page-num)))))))]

       (get-results 1)))))

