(ns java-integrity-api.http
  (:require [clj-http.client :as http]
            [clj-http.util :as util]
            [clojure.xml :as xml]
            [java-integrity-api.integrity :as i]))

(def url-encode util/url-encode)

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

(defn rails-params-hash-to-query-string [params]
  " clj-http unfortunately doesn't do this very well. Turned out to be quite
  a painful process"
  (let [lists-of-terms
        (for [[k v] params
              res (cond
                    (map? v) (map (fn [[keyname & r]] (cons (str "[" keyname "]") r) ) (query-to-string v))
                    (sequential? v) (map #(str "[]=" (url-encode %)) v)
                    :else [(str "=" (url-encode v))])]
          (flatten [k res]))]

    (clojure.string/join
      "&"
      (map #(apply str %) lists-of-terms))))

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

