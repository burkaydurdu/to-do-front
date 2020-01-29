(ns to-do.util
  (:require [clojure.string :as str]
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
