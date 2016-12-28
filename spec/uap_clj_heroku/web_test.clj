(ns uap-clj-heroku.web-test
  (:require [speclj.core :refer :all]
            [uap-clj-heroku.web :refer :all]
            [ring.mock.request :as mock]))

(describe "known routes"
  (it "GET /"
    (should= (app (mock/request :get "/"))
             {:status  200
              :headers {"Content-Type" "text/plain"}
              :body    "Useragent parser v1.3.1"})))
