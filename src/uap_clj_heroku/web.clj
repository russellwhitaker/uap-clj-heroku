(ns uap-clj-heroku.web
  (:require [compojure.core :refer [defroutes GET ANY]]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [clojure.java.io :as io]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.json :refer [wrap-json-params]]
            [cheshire.core :as json]
            [ring.middleware.stacktrace :as trace]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :as cookie]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [uap-clj.core :refer [useragent]]))

(defn lookup
  "Look up full useragent fields, or only lookup device,
   o/s, or browser fields.
  "
  [query]
  (let [{:keys [ua lookup]} query
        parser (if (= "useragent" lookup)
                 (symbol "uap-clj.core/useragent")
                 (symbol (str "uap-clj." lookup) lookup))]
    (if (= "useragent" lookup)
      ((resolve parser) ua)
      (merge {(keyword lookup) ((resolve parser) ua)}
             {:ua ua}))))

(defn lookups
  "Get request and look up against all useragents therein
  "
  [event]
  {:results (into [] (map lookup (:queries event)))})

(defroutes app
  (GET "/" []
       {:status 200
        :headers {"Content-Type" "text/plain"}
        :body "Useragent parser v1.3.1"})
  (ANY "/useragent" [& params]
       {:status 200
        :headers {"Content-Type" "application/json"}
        :body (json/generate-string (lookups params))})
  (ANY "*" []
       (route/not-found (slurp (io/resource "404.html")))))

(defn wrap-error-page
  [handler]
  (fn [req]
    (try (handler req)
         (catch Exception e
           {:status 500
            :headers {"Content-Type" "text/html"}
            :body (slurp (io/resource "500.html"))}))))

(defn wrap-app
  [app]
  ;; TODO: heroku config:add SESSION_SECRET=$RANDOM_16_CHARS
  (let [store (cookie/cookie-store {:key (env :session-secret)})]
    (-> app
        ((if (env :production)
           wrap-error-page
           trace/wrap-stacktrace))
        wrap-keyword-params
        wrap-json-params
        (site {:session {:store store}}))))

(defn -main
  [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (wrap-app #'app) {:port port :join? false})))
