(ns integrity-api.test.http
  (:use [integrity-api.http])
  (:use [midje.sweet]))

(def horrible-fixture-of-death
  {:tag :user-access-group-authorisation, :attrs nil,
   :content [{:tag :name, :attrs nil, :content ["root"]}
             {:tag :datasets, :attrs {:type "array"},
              :content [{:tag :dataset, :attrs nil,
                         :content [{:tag :id, :attrs nil, :content ["1"]}
                                   {:tag :name, :attrs nil, :content ["Regions"]}
                                   {:tag :is-bulk-allowed, :attrs nil, :content ["true"]}
                                   {:tag :is-incremental-allowed, :attrs nil, :content ["true"]}
                                   {:tag :table-name, :attrs nil, :content ["e_regions"]}
                                   {:tag :qualifiers, :attrs {:type "array"}, :content nil}
                                   {:tag :dataset-formats, :attrs {:type "array"},
                                    :content [{:tag :dataset-format, :attrs nil,
                                               :content [{:tag :name, :attrs nil, :content ["csv"]}
                                                         {:tag :parser-type, :attrs nil, :content ["CSV"]}]}]}]}
                        {:tag :dataset, :attrs nil,
                         :content [{:tag :id, :attrs nil, :content ["2"]}
                                   {:tag :name, :attrs nil, :content ["Sales People"]}
                                   {:tag :is-bulk-allowed, :attrs nil, :content ["true"]}
                                   {:tag :is-incremental-allowed, :attrs nil, :content ["true"]}
                                   {:tag :table-name, :attrs nil, :content ["e_sales_people"]}
                                   {:tag :qualifiers, :attrs {:type "array"},
                                    :content [{:tag :qualifier, :attrs nil,
                                               :content [{:tag :dataset-attribute-name, :attrs nil, :content ["Sales Region"]}
                                                         {:tag :valid-values, :attrs {:type "array"},
                                                          :content [{:tag :valid-value, :attrs nil, :content ["1"]}
                                                                    {:tag :valid-value, :attrs nil, :content ["2"]}
                                                                    {:tag :valid-value, :attrs nil, :content ["4"]}
                                                                    {:tag :valid-value, :attrs nil, :content ["3"]}]}]}]}
                                   {:tag :dataset-formats, :attrs {:type "array"},
                                    :content [{:tag :dataset-format, :attrs nil,
                                               :content [{:tag :name, :attrs nil, :content ["csv"]}
                                                         {:tag :parser-type, :attrs nil, :content ["CSV"]}]}]}]}]}]})

(fact "sample xml converts to clojure data"
      (rails-struct-map-to-data horrible-fixture-of-death) =>
      {:user-access-group-authorisation {:name "root"
                                         :datasets [{:id "1"
                                                     :name "Regions"
                                                     :is-bulk-allowed "true"
                                                     :is-incremental-allowed "true"
                                                     :table-name "e_regions"
                                                     :qualifiers []
                                                     :dataset-formats [{:name "csv" :parser-type "CSV"}]}
                                                    {:id "2"
                                                     :name "Sales People"
                                                     :is-bulk-allowed "true"
                                                     :is-incremental-allowed "true"
                                                     :table-name "e_sales_people"
                                                     :qualifiers [{:dataset-attribute-name "Sales Region"
                                                                   :valid-values ["1" "2" "4" "3"]}]
                                                     :dataset-formats [{:name "csv" :parser-type "CSV"}]}]}})
