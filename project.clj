(defproject uap-clj-heroku "1.0.0"
  :description "Heroku-specific REST API for the uap-clj useragent parser"
  :url "http://uap-clj-heroku.herokuapp.com"
  :license {:name "FIXME: choose"
            :url "http://example.com/FIXME"}
  :dependencies [[org.clojure/clojure       "1.8.0"]
                 [compojure                 "1.5.1"]
                 [ring/ring-jetty-adapter   "1.5.0"]
                 [ring/ring-devel           "1.5.0"]
                 [ring-basic-authentication "1.0.5"]
                 [environ                   "1.1.0"]
                 [com.cemerick/drawbridge   "0.0.6"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.2.1"]
            [lein-ancient         "0.6.10"]
            [jonase/eastwood      "0.2.3"]
            [speclj               "3.3.2"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "uap-clj-heroku-standalone.jar"
  :profiles {:production {:env {:production true}}
             :uberjar {:aot :all}
             :dev {:dependencies [[criterium      "0.4.4"]
                                  [speclj         "3.3.2"]
                                  [ring/ring-mock "0.3.0"]]
                   :test-paths ["spec"]}})
