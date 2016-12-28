(ns uap-clj-heroku.web-test
  (:require [speclj.core :refer :all]
            [uap-clj-heroku.web :refer :all]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.mock.request :as mock]))

(def ua "Lenovo-A288t_TD/S100 Linux/2.6.35 Android/2.3.5 Release/02.29.2012 Browser/AppleWebkit533.1 Mobile Safari/533.1 FlyFlow/1.4")

(describe "known routes"
  (it "GET /"
    (should= (app (mock/request :get "/"))
             {:status  200
              :headers {"Content-Type" "text/plain"}
              :body    "Useragent parser v1.3.1"}))
  (it "POST /useragent"
    (should= ((-> app wrap-params wrap-keyword-params)
               (mock/request :post "/useragent" {:useragent ua}))
             {:status  200
              :headers {"Content-Type" "application/json"}
              :body    {:ua ua
                        :browser
                          {:family "Baidu Explorer"
                           :major "1"
                           :minor "4"
                           :patch ""}
                        :os
                          {:family "Android"
                           :major "2"
                           :minor "3"
                           :patch "5"
                           :patch_minor ""}
                        :device
                          {:family "Lenovo A288t_TD"
                           :brand "Lenovo"
                           :model "A288t_TD"}}})))
