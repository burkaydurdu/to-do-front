(ns to-do.util
  (:require [clojure.string :as str]
            [cemerick.url :as url]
            [pushy.core :as pushy]
            [secretary.core :as secretary]
            [re-frame.core :refer [subscribe dispatch]]
            [antizer.reagent :as ant]
            [ajax.core :as ajax]))

(goog-define api-url "http://localhost:3011")

(def not-nil? (complement nil?))

(def not-empty? (complement empty?))

(def not-blank? (complement str/blank?))

(defn email?
  [email]
  (boolean
   (and (not (str/blank? email))
        (re-matches #"(([^<>()\[\]\\.,;:\s@\"]+(\.[^<>()\[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))" email))))

(defn dissoc-in
  ([m ks]
   (if-let [[k & ks] (seq ks)]
     (if (seq ks)
       (let [v (dissoc-in (get m k) ks)]
         (if (empty? v)
           (dissoc m k)
           (assoc m k v)))
       (dissoc m k))
     m))
  ([m ks & kss]
   (if-let [[ks' & kss] (seq kss)]
     (recur (dissoc-in m ks) ks' kss)
     (dissoc-in m ks))))

(defn dispatch-n
  [& args]
  (doseq [k args]
    (dispatch (if (vector? k) k (vector k)))))

(defn sleep
  [f ms]
  (js/setTimeout f ms))

(defn create-request-map
  ([type uri]
   (create-request-map type uri nil nil))
  ([type uri on-success]
   (create-request-map type uri on-success nil))
  ([type uri on-success on-fail]
   (cond-> {:headers         {"token" @(subscribe [:token])}
            :method          type
            :uri             (str api-url uri)
            :format          (ajax/json-request-format)
            :response-format (ajax/json-response-format {:keywords? true})
            :on-success      (if (vector? on-success) on-success [on-success])
            :on-failure      (if (vector? on-fail) on-fail [on-fail])}
     (nil? on-success) (assoc :on-success [:no-http-on-ok])
     (nil? on-fail) (assoc :on-failure [:no-http-on-failure]))))

(defn set-item!
  [key val]
  (.setItem (.-localStorage js/window) key (.stringify js/JSON (clj->js val))))

(defn get-item
  [key]
  (js->clj (.parse js/JSON (.getItem (.-localStorage js/window) key)) :keywordize-keys true))

(defn remove-items!
  [keys]
  (doseq [k keys]
    (.removeItem (.-localStorage js/window) k)))

(defn remove-item!
  [key]
  (remove-items! [key]))

(defn alert-action [message error?]
  (ant/notification-open {:type (if error? "error" "success")
                          :description message}))

(defn change-url
  [path]
  (set! window.location.href path))

(defn window-origin []
  (if-let [origin (-> js/window .-location .-origin)]
    origin
    (str (-> js/window .-location .-protocol) "//"
         (-> js/window .-location .-hostname)
         (if-let [port (-> js/window .-location .-port)]
           (str ":" port)
           ""))))

(defn set-uri-token!
  [uri]
  (let [u (url/url (window-origin))
        k (str u uri)]
    (pushy/set-token! to-do.routes/history k)
    (secretary/dispatch! uri)))

(defn get-url-params
  [url]
  (->> url
       (re-seq #"[?&]+([^=&]+)=([^&]*)")
       (map (fn [[_ k v]]
              [(keyword k) v]))
       (into {})))
