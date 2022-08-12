(ns routes
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.ring-middlewares :as http-middlewares]
            [hiccup.page :refer [html5]]
            [hiccup.form :as form]
            [validate :refer [validate-name]]))

(def debug
  {:name ::debug
   :enter
   (fn [context]
     (let [request (:request context)
           ;; ppp (println (keys request))
           ;; pp (select-keys request [;; :query-string
           ;;                          ;; :path-params
           ;;                          ;; :body
           ;;                          :content-type
           ;;                          ;; :edn-params
           ;;                          :params
           ;;                          :multipart-params])
           ;; p (println "print request " pp)
           ;; edn-params (:edn-params request)
           ;; res     {:status 200 :body ["hellow from debug"]}
           _  (println "contextnya ini bro: " context " end context")
           __ (println "requestnya ini bro: " request " end request")]
       context))})

;; === assets ===
(defn hiccup-common [& body]
  (html5
   [:head
    [:title "myapp-pedestal"]
    [:style
     "div" {:display "flex"}]]
   [:body body]))

(defn error-component [err]
  (when (not (nil? err))
    [:p
     [:span {:style "color:red"}
      (:username err)]]))

;; === sistem ===
(defn common-respond [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn hiccup-hello [uname error]
  (let [name (:username uname)]
    (hiccup-common
     [:h1 (str "Hello, " (if error "Folks" name) \! " " uname)]
     (error-component error)
     [:div
      "Masukin nama lo: "
      (form/form-to
       [:post "/greet"]
       [:input {:type "text"
                :id   "name"
                :name "username"}]
       (form/submit-button "Enter"))])))

(def get-params
  {:name ::get-params
   :enter
   (fn [{:keys [request] :as context}]
     (let [name (get-in request [:params "username"])
          ;;  _ (println "keys : " (:body request))
           __ (println "requestnya: " request)
           ]
       (assoc context :username name)))})

(def hello-interceptor
  {:name ::say-hello
   :enter
   (fn [{:keys [request username] :as context}]
     (let [validate  (partial validate-name {:username username})
           name      (validate :value)
           error     (validate :error)
           hello     (-> (hiccup-hello name error)
                         common-respond)]
       (assoc context :response hello)))})

(def common-interceptors [(body-params/body-params) http/html-body (http-middlewares/multipart-params)])

(def routes
  #{["/greet" :get [hello-interceptor] :route-name :greet-get]
    ["/greet" :post (conj common-interceptors get-params hello-interceptor) :route-name :greet-post]
    ;; ["/greet" :post [get-params hello-interceptor] :route-name :greet-post]
    })
