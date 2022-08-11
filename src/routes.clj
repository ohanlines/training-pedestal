(ns routes
  (:require [hiccup.page :refer [html5]]
            [hiccup.form :as form]
            [validate :refer [validate-name]]
            ))

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
       context))
   })

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

;; === get greet ===
(defn common-respond [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn hiccup-hello [name error]
  (let [name (:username name)]
    (hiccup-common
     [:h1 (str "Hello, " (if error "Folks" name) \!)]
     (error-component error)
     [:div
      "Masukin nama lo: "
      (form/form-to
       [:post "/greet"] ;; method post
      ;;  [:get "/greet"]
       [:input {:type "text"
                :id   "name"
                :name "username"}]
       (form/submit-button "Enter"))])
    ))

(def get-params
  {:name ::get-params
   :enter
   (fn [{:keys [request] :as context}]
     (let [name (get-in request [:params "username"])]
       (assoc context :username name)))
   })

(def hello-interceptor
  {:name ::say-hello
   :enter
   (fn [{:keys [request username] :as context}]
     (let [validate  (partial validate-name {:username username})
           name      (validate :value)
           error     (validate :error)
           hello     (-> (hiccup-hello name error)
                         common-respond)]
       (assoc context :response hello))
     
    ;;  (let [init-name (get-in request [:query-params :usernames])
    ;;        validate  (partial validate-name {:username init-name})
    ;;        name      (validate :value)
    ;;        error     (validate :error)
    ;;        hello     (-> (hiccup-hello name error)
    ;;                      common-respond)]
    ;;    (assoc context :respose hello)
    ;;    )
     )})

;; (defn hello-world [request]
;;   {:status 200 :body "Hello, world!"})

;; (defn coba [request]
;;   (-> (hiccup-hello "ohan" nil)
;;       common-respond))

(def routes
  #{
    ;; ["/greet" :get hello-world :route-name :greet]
    ;; ["/greet" :get coba :route-name :greet]
    ;; ["/greet" :get [get-params hello-interceptor] :route-name :greet-get]
    ["/greet" :post [get-params hello-interceptor] :route-name :greet-post]
    })
