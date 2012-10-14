(ns integrity-api.http
  (:require [clj-http.client :as http]
            [clj-http.util :as urls]
            [clojure.xml :as xml]
            [integrity-api.integrity :as i]
            [integrity-api.util :as util]
            [clojure.walk :as trees]))

(def url-encode urls/url-encode)

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

(defn walk-transform-map [f a-map]
  (trees/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) a-map))

(defn surround-keys-with-brackets [a-map]
  "{a {b {c d}}} => {[a] {[b] {[c] d}}}"
  (let [surround (fn [[k v]] [(str "[" k "]") v])]
    (walk-transform-map surround a-map)))

(defn add-assign-chars [params]
  "{a {b {c [d e], f g} } } => {a {b {c[]= [d e], f= g}}}"
  (let [add-chars (fn [[k v]] (cond
                                (map? v) [k v]
                                (sequential? v) [(str k "[]=") v]
                                :else [(str k "=") v]))]
    (walk-transform-map add-chars params)))

(defn urlencode-values [params]
  "performs urlencode on the string values at the leaves"
  (let [encode (fn [[k v]] (cond
                             (map? v) [k v]
                             (sequential? v) [k (map url-encode v)]
                             :else [k (url-encode (str v))]))]
    (walk-transform-map encode params)))

(defn rails-params-hash-to-query-string [params]
  " clj-http unfortunately doesn't do this very well. Turned out to be quite
  a painful process"
  (let [brackets-added-subkeys (into params
                                     (map (fn [[k v]] [k (surround-keys-with-brackets v)])
                                          (filter (fn [[k v]] (map? v)) params)))

        formatted (urlencode-values (add-assign-chars brackets-added-subkeys))]

    (clojure.string/join "&" (map #(apply str %) (util/flatten-tree formatted)))))

(defn integrity-get
  ([integrity path] (integrity-get integrity path nil))

  ([integrity path params]
   (let [path-and-query (if params (str path "?" (rails-params-hash-to-query-string params)) path)]
     (rails-xml-to-data
       (:body
         (http/get (str (i/host integrity) "/" path-and-query)
                   {:basic-auth [(i/user integrity) (i/password integrity)]
                    :content-type :xml}))))))

(defn get-config [integrity]
  (integrity-get integrity "upload_attempts/new.xml"))

