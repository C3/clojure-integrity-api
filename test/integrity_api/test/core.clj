(ns integrity-api.test.core
  (:use [integrity-api.core])
  (:use [midje.sweet]))

(fact "I can get some rows from the uat server"
  (let [host (login "http://uat.c3hosted.com" "java_api" "thyhidema")
        result (search host "victorian cities")]
    result => (comp not empty?)))

