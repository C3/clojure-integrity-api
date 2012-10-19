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

(defn activity [integrity]
  (let [path "api/activity.xml"
        session (i/session integrity)
        per-page 100]

    (letfn [(do-request [max-activity-num]
              (i/activity-results
                (if max-activity-num
                  (service/integrity-get session path {:limit per-page :max_activity_num max-activity-num})
                  (service/integrity-get session path {:limit per-page}))))


            (activities-older-than [oldest-activity-num]
              (lazy-seq
                (let [activities (do-request oldest-activity-num)]
                  (if (empty? activities)
                    []
                    (concat activities (activities-older-than (dec (read-string (:activity-num (last activities))))))))))]

      (activities-older-than nil))))

(defn dataset-configs [integrity]
  (i/datasets (i/config integrity)))
