(ns uap-clj-heroku.web-test
  (:refer-clojure :exclude [read])
  (:require [speclj.core :refer :all]
            [uap-clj-heroku.web :refer :all]
            [clojure.java.io :as io :refer [reader]]
            [cheshire.core :as json]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.mock.request :as mock]))

(def ua "Lenovo-A288t_TD/S100 Linux/2.6.35 Android/2.3.5 Release/02.29.2012 Browser/AppleWebkit533.1 Mobile Safari/533.1 FlyFlow/1.4")

(def full-req (json/parse-stream (io/reader "dev-resources/post_lookup_all.json") true))
(def multiple-req (json/parse-stream (io/reader "dev-resources/post_lookup_multiple.json") true))
(def os-req (json/parse-stream (io/reader "dev-resources/post_lookup_os.json") true))
(def device-req (json/parse-stream (io/reader "dev-resources/post_lookup_device.json") true))
(def browser-req (json/parse-stream (io/reader "dev-resources/post_lookup_browser.json") true))

(describe "known routes"
  (it "GET /"
    (should= (app (mock/request :get "/"))
             {:status  200
              :headers {"Content-Type" "text/plain"}
              :body    "Useragent parser v1.3.1"}))
  (it "POST /useragent"
    (should= (update-in ((-> app wrap-keyword-params wrap-json-params)
                          (mock/content-type
                            (mock/request :post "/useragent" (json/generate-string full-req))
                            "application/json"))
                        [:body] #(json/parse-string % true))
             {:status  200
              :headers {"Content-Type" "application/json"}
              :body    {:results [{:ua ua
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
                           :model "A288t_TD"}}]}})
    (should== {:status  200
               :headers {"Content-Type" "application/json"}
               :body    {:results [{:ua ua
                                    :os
                                      {:family "Android"
                                       :major "2"
                                       :minor "3"
                                       :patch "5"
                                       :patch_minor ""}}]}}
              (update-in ((-> app wrap-keyword-params wrap-json-params)
                           (mock/content-type
                             (mock/request :post "/useragent" (json/generate-string os-req))
                             "application/json"))
                         [:body] #(json/parse-string % true)))
     (should== {:status  200
                :headers {"Content-Type" "application/json"}
                :body    {:results [{:ua ua
                                     :browser
                                       {:family "Baidu Explorer"
                                        :major "1"
                                        :minor "4"
                                        :patch ""}}]}}
               (update-in ((-> app wrap-keyword-params wrap-json-params)
                            (mock/content-type
                              (mock/request :post "/useragent" (json/generate-string browser-req))
                              "application/json"))
                          [:body] #(json/parse-string % true)))
      (should== {:status  200
                 :headers {"Content-Type" "application/json"}
                 :body    {:results [{:ua ua
                                      :device
                                        {:family "Lenovo A288t_TD"
                                         :brand "Lenovo"
                                         :model "A288t_TD"}}]}}
                (update-in ((-> app wrap-keyword-params wrap-json-params)
                             (mock/content-type
                               (mock/request :post "/useragent" (json/generate-string device-req))
                               "application/json"))
                           [:body] #(json/parse-string % true)))
       (should== {:status  200
                  :headers {"Content-Type" "application/json"}
                  :body    {:results
                             [{:ua
                                 "Lenovo-A288t_TD/S100 Linux/2.6.35 Android/2.3.5 Release/02.29.2012 Browser/AppleWebkit533.1 Mobile Safari/533.1 FlyFlow/1.4",
                               :browser
                                 {:family "Baidu Explorer", :major "1", :minor "4", :patch ""},
                               :os
                                 {:family "Android",
                                  :major "2",
                                  :minor "3",
                                  :patch "5",
                                  :patch_minor ""},
                               :device
                                 {:family "Lenovo A288t_TD", :brand "Lenovo", :model "A288t_TD"}}
                              {:browser {:family "Firefox", :major "3", :minor "0", :patch "19"},
                               :ua
                                 "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.19) Gecko/2010031218 FreeBSD/i386 Firefox/3.0.19,gzip(gfe),gzip(gfe)"}
                              {:device
                                {:family "HTC Amaze 4G", :brand "HTC", :model "Amaze 4G"},
                               :ua
                                 "Mozilla/5.0 (Linux; U; Android 4.0.3; en-us; Amaze_4G Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"}
                              {:os
                                {:family "Android",
                                 :major "2",
                                 :minor "3",
                                 :patch "6",
                                 :patch_minor ""},
                               :ua
                                 "UCWEB/2.0 (Linux; U; Adr 2.3.6; en-US; HUAWEI_Y210-0251) U2/1.0.0 UCBrowser/8.6.0.318 U2/1.0.0 Mobile"}]}}
                 (update-in ((-> app wrap-keyword-params wrap-json-params)
                              (mock/content-type
                                (mock/request :post "/useragent" (json/generate-string multiple-req))
                                "application/json"))
                            [:body] #(json/parse-string % true)))))
