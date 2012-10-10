(ns integrity-api.test.core
  (:use [integrity-api.core])
  (:use [clojure.test]))

(deftest uat-integration
  (let [host (login "http://uat.c3hosted.com" "java_api" "thyhidema")
        result (search host "victorian cities")]
    (is (not (empty? result)))))

