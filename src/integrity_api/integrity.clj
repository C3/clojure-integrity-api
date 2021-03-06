(ns integrity-api.integrity)

(defrecord Session [host user password])

(defrecord Integrity [session config])

(def session :session)
(def config :config)

(defn mksession [host user password] (Session. host user password))
(defn mkintegrity [session config] (Integrity. session config))

(def user :user)
(def password :password)
(def host :host)

(defn datasets [config]
  (reduce #(assoc %1 (:id %2) %2)
          {}
          (:datasets
            (:user-access-group-authorisation config))))

(def dataset-name :name)
(def dataset-id :id)

(defn find-dataset [integrity name-to-find]
  (first (filter #(= name-to-find (dataset-name %)) (vals (datasets (config integrity))))))

(defn qualifiers-to-params [qualifiers]
  (let [qualifier-params
        (map (fn [[attrib-name values]]
                         {"qualifier_values" values
                          "dataset_attribute_name" attrib-name})
             qualifiers)

        numbered-qualifier-hash
        (reduce merge
                (map-indexed hash-map qualifier-params))]

    {"upload_qualifiers" numbered-qualifier-hash}))

(defn available-qualifiers [integrity dataset-name]
  (let [dataset (find-dataset integrity dataset-name)]
    (if dataset
      (reduce merge
        (map #(hash-map (:dataset-attribute-name %1) (:valid-values %1))
             (:qualifiers dataset)))

      (throw (Exception. "No such dataset")))))

(def activity-results :activities)

(defn search-results [service-output] (first (vals service-output)))
