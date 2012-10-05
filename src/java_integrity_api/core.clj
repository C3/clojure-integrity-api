(ns java-integrity-api.core
  (:require [clj-http.client :as http]
            [clojure.xml :as xml])
  (:import [java.net URLEncoder]))

(defrecord Session [host user password])

(defrecord Integrity [session config])

(def session :session)
(def config :config)

(defn mksession [host user password] (Session. host user password))

(def user :user)
(def password :password)
(def host :host)

(defn get-struct-map [xml]
  (let [stream (java.io.ByteArrayInputStream. (.getBytes xml))]
    (xml/parse stream)))

(defn rails-struct-map-to-data [garbage]
  (letfn [(convert-item [item]
            (if (map? item)
              (let [{:keys [tag content attrs]} item]
                (if (and attrs (:type attrs) (= "array" (:type attrs)))
                  {tag (mapcat vals (map convert-item content))}
                  {tag (reduce merge (map convert-item content))}))
              item))]
    (convert-item garbage)))

(defn rails-xml-to-data [xml]
  (rails-struct-map-to-data (get-struct-map xml)))

(defn urlencode-path [path] (URLEncoder/encode path "UTF-8"))

(defn integrity-get [session path]
  (rails-xml-to-data
    (:body
      (http/get (str (host session) "/" path)
                {:basic-auth [(user session) (password session)]
                 :content-type :xml}))))

(defn datasets [config]
  (:datasets
    (:user-access-group-authorisation config)))

(defn get-config [session]
  (integrity-get session "upload_attempts/new.xml"))

(defn login [hostname user password]
  (let [session (mksession hostname user password)]
    (Integrity. session (get-config session))))

(defn available-datasets [integrity]
  (map :name (datasets (config integrity))))

(defn find-dataset [integrity dataset-name]
  (first (filter #(= dataset-name (:name %)) (datasets (config integrity)))))

(defn available-qualifiers [integrity dataset-name]
  (let [dataset (find-dataset integrity dataset-name)]
    (if dataset
      (reduce merge
        (map #(hash-map (:dataset-attribute-name %1) (:valid-values %1))
             (:qualifiers dataset)))

      (throw (Exception. "No such dataset")))))

(defn qualifiers-to-query-params [qualifiers]
  (let [value-params
        (for [[idx [attribute values]] (map-indexed list qualifiers)
              value values]
          (str "upload_qualifiers[" idx "][qualifier_values][]=" (urlencode-path value)))]


    (clojure.string/join
      "&"
      (concat value-params (map-indexed (fn [idx [attribute values]]
                                          (str "upload_qualifiers[" idx "][dataset_attribute_name]=" (urlencode-path attribute)))
                                        qualifiers)))))



(defn search
  ([integrity dataset-name] (search integrity dataset-name (available-qualifiers integrity dataset-name)))

  ([integrity dataset-name qualifiers]
   (let [dataset-id (:id (find-dataset integrity dataset-name))
         path (str "datasets/" dataset-id "/search_results.xml")]

     (integrity-get (session integrity)
                    (str path "?" (qualifiers-to-query-params qualifiers))))))

